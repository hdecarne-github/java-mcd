/*
 * Copyright (c) 2019-2020 Holger de Carne and contributors, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.carne.mcd.x86.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import de.carne.boot.check.Check;
import de.carne.boot.logging.Log;
import de.carne.mcd.bootstrap.InstructionReferenceEntry;
import de.carne.mcd.instruction.InstructionOpcode;
import de.carne.util.Strings;

final class X86InstructionReferenceScraper extends DefaultHandler implements Iterable<X86InstructionReferenceEntry> {

	private static final Log LOG = new Log();

	private static final String PATH_ONE_BYTE_PRI_OPCD = "x86reference/one-byte/pri_opcd";
	private static final String PATH_TWO_BYTE_PRI_OPCD = "x86reference/two-byte/pri_opcd";
	private static final String PATH_ONE_BYTE_ENTRY = "x86reference/one-byte/pri_opcd/entry";
	private static final String PATH_TWO_BYTE_ENTRY = "x86reference/two-byte/pri_opcd/entry";
	private static final String PATH_ONE_BYTE_ENTRY_PREF = "x86reference/one-byte/pri_opcd/entry/pref";
	private static final String PATH_TWO_BYTE_ENTRY_PREF = "x86reference/two-byte/pri_opcd/entry/pref";
	private static final String PATH_ONE_BYTE_ENTRY_SEC_OPCD = "x86reference/one-byte/pri_opcd/entry/sec_opcd";
	private static final String PATH_TWO_BYTE_ENTRY_SEC_OPCD = "x86reference/two-byte/pri_opcd/entry/sec_opcd";
	private static final String PATH_ONE_BYTE_ENTRY_OPCD_EXT = "x86reference/one-byte/pri_opcd/entry/opcd_ext";
	private static final String PATH_TWO_BYTE_ENTRY_OPCD_EXT = "x86reference/two-byte/pri_opcd/entry/opcd_ext";
	private static final String PATH_ONE_BYTE_ENTRY_PROC_END = "x86reference/one-byte/pri_opcd/entry/proc_end";
	private static final String PATH_TWO_BYTE_ENTRY_PROC_END = "x86reference/two-byte/pri_opcd/entry/proc_end";
	private static final String PATH_ONE_BYTE_ENTRY_SYNTAX = "x86reference/one-byte/pri_opcd/entry/syntax";
	private static final String PATH_TWO_BYTE_ENTRY_SYNTAX = "x86reference/two-byte/pri_opcd/entry/syntax";
	private static final String PATH_ONE_BYTE_ENTRY_SYNTAX_MNEM = "x86reference/one-byte/pri_opcd/entry/syntax/mnem";
	private static final String PATH_TWO_BYTE_ENTRY_SYNTAX_MNEM = "x86reference/two-byte/pri_opcd/entry/syntax/mnem";
	private static final String PATH_ONE_BYTE_ENTRY_SYNTAX_DST = "x86reference/one-byte/pri_opcd/entry/syntax/dst";
	private static final String PATH_TWO_BYTE_ENTRY_SYNTAX_DST = "x86reference/two-byte/pri_opcd/entry/syntax/dst";
	private static final String PATH_ONE_BYTE_ENTRY_SYNTAX_SRC = "x86reference/one-byte/pri_opcd/entry/syntax/src";
	private static final String PATH_TWO_BYTE_ENTRY_SYNTAX_SRC = "x86reference/two-byte/pri_opcd/entry/syntax/src";

	private Deque<X86InstructionReferenceEntry> entries = new LinkedList<>();
	private final X86Mode scrapeMode;

	private final Deque<String> xmlPathStack = new LinkedList<>();

	// x86reference/two-byte/pri_opcd
	private int twoBytePrefixByte = -1;
	// x86reference/*/pri_opcd
	private int priOpcode = -1;
	// x86reference/*/pri_opcd/entry
	private String mode = InstructionReferenceEntry.NO_VALUE;
	private String sFlag = InstructionReferenceEntry.NO_VALUE;
	private String dFlag = InstructionReferenceEntry.NO_VALUE;
	private String rFlag = InstructionReferenceEntry.NO_VALUE;
	// x86reference/*/pri_opcd/entry/pref
	private int prefixByte = -1;
	// x86reference/*/pri_opcd/entry/sec_opcd
	private int secOpcode = -1;
	// x86reference/*/pri_opcd/entry/opcd_ext
	private String signature = "";
	// x86reference/*/pri_opcd/entry/proc_end
	private String procEnd = InstructionReferenceEntry.NO_VALUE;
	// x86reference/*/pri_opcd/entry/syntax
	private int syntaxCount = 0;
	// x86reference/*/pri_opcd/entry/syntax/mnem
	private String mnemonic = InstructionReferenceEntry.NO_VALUE;
	// x86reference/*/pri_opcd/entry/syntax/*
	private String opDisplayed = InstructionReferenceEntry.NO_VALUE;
	private String opNr = InstructionReferenceEntry.NO_VALUE;
	private String opAddress = InstructionReferenceEntry.NO_VALUE;

	private final StringBuilder characterBuffer = new StringBuilder();
	private boolean characterBufferEnabled;

	X86InstructionReferenceScraper(X86Mode scrapMode) {
		this.scrapeMode = scrapMode;
	}

	public void scrape(String source) throws IOException {
		try (InputStream sourceStream = new URL(source).openStream()) {
			SAXParserFactory spf = SAXParserFactory.newInstance();

			spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

			SAXParser sp = spf.newSAXParser();

			this.twoBytePrefixByte = -1;
			sp.parse(sourceStream, this);
		} catch (SAXException | ParserConfigurationException e) {
			throw new IOException("XML processing failure", e);
		}
	}

	private void resetPriOpcode(int prefix, byte opcode) {
		this.twoBytePrefixByte = prefix;
		this.priOpcode = Byte.toUnsignedInt(opcode);
		resetEntry();
	}

	private void resetEntry() {
		this.mode = "r";
		this.sFlag = InstructionReferenceEntry.NO_VALUE;
		this.dFlag = InstructionReferenceEntry.NO_VALUE;
		this.rFlag = InstructionReferenceEntry.NO_VALUE;
		this.prefixByte = -1;
		this.secOpcode = -1;
		this.signature = "";
		this.procEnd = InstructionReferenceEntry.NO_VALUE;
		this.syntaxCount = 0;
		resetSyntax();
	}

	private void resetSyntax() {
		this.mnemonic = InstructionReferenceEntry.NO_VALUE;
		resetOperand();
	}

	private void resetOperand() {
		this.opDisplayed = InstructionReferenceEntry.NO_VALUE;
		this.opNr = InstructionReferenceEntry.NO_VALUE;
		this.opAddress = InstructionReferenceEntry.NO_VALUE;
	}

	private void enableCharacterBuffer() {
		Check.assertTrue(!this.characterBufferEnabled);

		this.characterBufferEnabled = true;
	}

	private String disableCharacterBuffer() {
		Check.assertTrue(this.characterBufferEnabled);

		String characters = this.characterBuffer.toString();

		this.characterBuffer.setLength(0);
		this.characterBufferEnabled = false;
		return characters;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		String xmlPath = (this.xmlPathStack.isEmpty() ? "" : this.xmlPathStack.peek() + "/") + qName;

		this.xmlPathStack.push(xmlPath);

		switch (xmlPath) {
		case PATH_ONE_BYTE_PRI_OPCD:
			startPriOpcdElement(-1, attributes);
			break;
		case PATH_TWO_BYTE_PRI_OPCD:
			startPriOpcdElement(0x0f, attributes);
			break;
		case PATH_ONE_BYTE_ENTRY:
		case PATH_TWO_BYTE_ENTRY:
			startEntryElement(attributes);
			break;
		case PATH_ONE_BYTE_ENTRY_PREF:
		case PATH_TWO_BYTE_ENTRY_PREF:
			startPrefElement();
			break;
		case PATH_ONE_BYTE_ENTRY_SEC_OPCD:
		case PATH_TWO_BYTE_ENTRY_SEC_OPCD:
			startSecOpcdElement();
			break;
		case PATH_ONE_BYTE_ENTRY_OPCD_EXT:
		case PATH_TWO_BYTE_ENTRY_OPCD_EXT:
			startOpcdExtElement();
			break;
		case PATH_ONE_BYTE_ENTRY_PROC_END:
		case PATH_TWO_BYTE_ENTRY_PROC_END:
			startProcEndElement();
			break;
		case PATH_ONE_BYTE_ENTRY_SYNTAX:
		case PATH_TWO_BYTE_ENTRY_SYNTAX:
			startSyntaxElement();
			break;
		case PATH_ONE_BYTE_ENTRY_SYNTAX_MNEM:
		case PATH_TWO_BYTE_ENTRY_SYNTAX_MNEM:
			startMnemElement();
			break;
		case PATH_ONE_BYTE_ENTRY_SYNTAX_DST:
		case PATH_TWO_BYTE_ENTRY_SYNTAX_DST:
		case PATH_ONE_BYTE_ENTRY_SYNTAX_SRC:
		case PATH_TWO_BYTE_ENTRY_SYNTAX_SRC:
			startDstSrcElement(attributes);
			break;
		default:
			// do nothing
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String xmlPath = this.xmlPathStack.pop();

		switch (xmlPath) {
		case PATH_ONE_BYTE_ENTRY:
		case PATH_TWO_BYTE_ENTRY:
			endEntryElement();
			break;
		case PATH_ONE_BYTE_ENTRY_PREF:
		case PATH_TWO_BYTE_ENTRY_PREF:
			endPrefElement();
			break;
		case PATH_ONE_BYTE_ENTRY_SEC_OPCD:
		case PATH_TWO_BYTE_ENTRY_SEC_OPCD:
			endSecOpcdElement();
			break;
		case PATH_ONE_BYTE_ENTRY_OPCD_EXT:
		case PATH_TWO_BYTE_ENTRY_OPCD_EXT:
			endOpcdExtElement();
			break;
		case PATH_ONE_BYTE_ENTRY_PROC_END:
		case PATH_TWO_BYTE_ENTRY_PROC_END:
			endProcEndElement();
			break;
		case PATH_ONE_BYTE_ENTRY_SYNTAX:
		case PATH_TWO_BYTE_ENTRY_SYNTAX:
			endSyntaxElement();
			break;
		case PATH_ONE_BYTE_ENTRY_SYNTAX_MNEM:
		case PATH_TWO_BYTE_ENTRY_SYNTAX_MNEM:
			endMnemElement();
			break;
		case PATH_ONE_BYTE_ENTRY_SYNTAX_DST:
		case PATH_TWO_BYTE_ENTRY_SYNTAX_DST:
		case PATH_ONE_BYTE_ENTRY_SYNTAX_SRC:
		case PATH_TWO_BYTE_ENTRY_SYNTAX_SRC:
			endDstSrcElement();
			break;
		default:
			// do nothing
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (this.characterBufferEnabled) {
			this.characterBuffer.append(ch, start, length);
		}
	}

	private void startPriOpcdElement(int prefix, Attributes attributes) throws SAXException {
		String opcodeString = safeGetAttribute(attributes, "value");

		LOG.debug("Processing primary opcode ''{0}''...", opcodeString);

		byte opcode = parseOpcode(opcodeString);

		resetPriOpcode(prefix, opcode);
	}

	private void startEntryElement(Attributes attributes) {
		this.mode = getOptionalAttribute(attributes, "mode", this.mode);
		this.dFlag = getOptionalAttribute(attributes, "direction", this.dFlag);
		this.sFlag = getOptionalAttribute(attributes, "op_size", this.sFlag);
		this.rFlag = getOptionalAttribute(attributes, "r", this.rFlag);
	}

	private void endEntryElement() {
		if (this.mnemonic.equals(InstructionReferenceEntry.NO_VALUE)) {
			X86InstructionReferenceEntry entry = this.entries.getLast();

			if ("e".equals(this.mode)) {
				entry.disableX86b64();
			}
		}
		resetEntry();
	}

	private void startPrefElement() {
		enableCharacterBuffer();
	}

	private void endPrefElement() throws SAXException {
		String prefixString = disableCharacterBuffer();

		this.prefixByte = Byte.toUnsignedInt(parseOpcode(prefixString));
	}

	private void startSecOpcdElement() {
		enableCharacterBuffer();
	}

	private void endSecOpcdElement() throws SAXException {
		String opcodeString = disableCharacterBuffer();

		this.secOpcode = Byte.toUnsignedInt(parseOpcode(opcodeString));
	}

	private void startOpcdExtElement() {
		enableCharacterBuffer();
	}

	private void endOpcdExtElement() {
		this.signature = "/" + disableCharacterBuffer() + ":";
	}

	private void startProcEndElement() {
		enableCharacterBuffer();
	}

	private void endProcEndElement() {
		this.procEnd = disableCharacterBuffer();
	}

	private void startSyntaxElement() {
		this.syntaxCount++;
	}

	private void endSyntaxElement() {
		if (!this.mnemonic.equals(InstructionReferenceEntry.NO_VALUE) && this.syntaxCount == 1) {
			InstructionOpcode opcode = getOpcode();

			for (String expandedSignature : expandSignature()) {
				LOG.info("Considering new opcode {0} {1}", opcode, this.mnemonic);

				X86InstructionReferenceEntry entry = new X86InstructionReferenceEntry(opcode, this.mnemonic,
						expandedSignature);

				if ("p".equals(this.mode) && InstructionReferenceEntry.NO_VALUE.equals(this.procEnd)) {
					entry.disableX86b16();
				} else if ("e".equals(this.mode) && InstructionReferenceEntry.NO_VALUE.equals(this.procEnd)) {
					entry.disableX86b16();
					entry.disableX86b32();
				} else if (!"r".equals(this.mode)) {
					entry.disableX86b16();
					entry.disableX86b32();
					entry.disableX86b64();
				}
				this.entries.add(entry);
				opcode = nextOpcode(opcode);
			}
		}
		resetSyntax();
	}

	private List<String> expandSignature() {
		List<String> signatures = new ArrayList<>(8);
		if (this.signature.contains(X86Symbol.OPCD_R8.symbol())) {
			signatures.add(this.signature.replace(X86Symbol.OPCD_R8.symbol(), X86Symbol.AL.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R8.symbol(), X86Symbol.CL.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R8.symbol(), X86Symbol.DL.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R8.symbol(), X86Symbol.BL.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R8.symbol(), X86Symbol.AH.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R8.symbol(), X86Symbol.CH.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R8.symbol(), X86Symbol.DH.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R8.symbol(), X86Symbol.BH.symbol()));
		} else if (this.signature.contains(X86Symbol.OPCD_R16.symbol())) {
			signatures.add(this.signature.replace(X86Symbol.OPCD_R16.symbol(), X86Symbol.AX.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R16.symbol(), X86Symbol.CX.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R16.symbol(), X86Symbol.DX.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R16.symbol(), X86Symbol.BX.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R16.symbol(), X86Symbol.SP.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R16.symbol(), X86Symbol.BP.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R16.symbol(), X86Symbol.SI.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R16.symbol(), X86Symbol.DI.symbol()));
		} else if (this.signature.contains(X86Symbol.OPCD_R32.symbol())) {
			signatures.add(this.signature.replace(X86Symbol.OPCD_R32.symbol(), X86Symbol.EAX.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R32.symbol(), X86Symbol.ECX.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R32.symbol(), X86Symbol.EDX.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R32.symbol(), X86Symbol.EBX.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R32.symbol(), X86Symbol.ESP.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R32.symbol(), X86Symbol.EBP.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R32.symbol(), X86Symbol.ESI.symbol()));
			signatures.add(this.signature.replace(X86Symbol.OPCD_R32.symbol(), X86Symbol.EDI.symbol()));
		} else {
			signatures.add(this.signature);
		}
		return signatures;
	}

	private InstructionOpcode nextOpcode(InstructionOpcode opcode) {
		byte[] opcodeBytes = opcode.bytes();

		opcodeBytes[opcodeBytes.length - 1]++;
		return InstructionOpcode.wrap(opcodeBytes);
	}

	private void startMnemElement() {
		enableCharacterBuffer();
	}

	private void endMnemElement() {
		this.mnemonic = disableCharacterBuffer().toLowerCase();
		if (Strings.notEmpty(this.signature)) {
			this.signature += this.mnemonic;
		}
	}

	private void startDstSrcElement(Attributes attributes) {
		enableCharacterBuffer();
		this.opDisplayed = getOptionalAttribute(attributes, "displayed", this.opDisplayed);
		this.opNr = getOptionalAttribute(attributes, "nr", this.opNr);
		this.opAddress = getOptionalAttribute(attributes, "address", this.opAddress);
	}

	private void endDstSrcElement() {
		String operandString = disableCharacterBuffer();

		if (!"no".equals(this.opDisplayed)) {
			String operand = this.scrapeMode.decodeOperandString(operandString);

			if (Strings.notEmpty(this.signature)) {
				this.signature += ",";
			}
			this.signature += operand;
		}
		resetOperand();
	}

	@Override
	public void endDocument() throws SAXException {
		Iterator<X86InstructionReferenceEntry> entryIterator = this.entries.iterator();
		Map<InstructionOpcode, X86InstructionReferenceEntry> packedEntries = new HashMap<>();

		while (entryIterator.hasNext()) {
			X86InstructionReferenceEntry entry = entryIterator.next();

			if (this.scrapeMode.isAvailable(entry)) {
				InstructionOpcode entryOpcode = entry.opcode();
				X86InstructionReferenceEntry packedEntry = packedEntries.get(entryOpcode);

				if (packedEntry != null) {
					if (packedEntry.isOpcdExt()) {
						packedEntry.addExtraFields(entry.extraFields());
					}
					entryIterator.remove();
				} else {
					packedEntries.put(entryOpcode, entry);
				}
			} else {
				entryIterator.remove();
			}
		}
	}

	private InstructionOpcode getOpcode() {
		Check.assertTrue(this.priOpcode >= 0);

		byte[] opcode = new byte[4];
		int opcodeLength = 0;

		if (this.prefixByte >= 0) {
			opcode[opcodeLength++] = (byte) this.prefixByte;
		}
		if (this.twoBytePrefixByte >= 0) {
			opcode[opcodeLength++] = (byte) this.twoBytePrefixByte;
		}
		opcode[opcodeLength++] = (byte) this.priOpcode;
		if (this.secOpcode >= 0) {
			opcode[opcodeLength++] = (byte) this.secOpcode;
		}
		return InstructionOpcode.wrap(opcode, 0, opcodeLength);
	}

	private static byte parseOpcode(String s) throws SAXException {
		int opcode;

		try {
			opcode = Integer.parseUnsignedInt(s, 16);
			if (opcode > 255) {
				throw new NumberFormatException("Invalid byte value: " + s);
			}
		} catch (NumberFormatException e) {
			throw new SAXParseException("Failed to parse opcode: " + s, null, e);
		}
		return (byte) opcode;
	}

	private static String getOptionalAttribute(Attributes attributes, String qName, String defaultValue) {
		String value = attributes.getValue(qName);

		return (value != null ? value : defaultValue);
	}

	private static String safeGetAttribute(Attributes attributes, String qName) throws SAXException {
		String value = attributes.getValue(qName);

		if (value == null) {
			throw new SAXParseException("Missing attribute: " + qName, null);
		}
		return value;
	}

	@Override
	public Iterator<X86InstructionReferenceEntry> iterator() {
		return this.entries.iterator();
	}

}
