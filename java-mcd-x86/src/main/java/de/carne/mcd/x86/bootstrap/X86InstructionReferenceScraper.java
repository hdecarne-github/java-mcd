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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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
import de.carne.mcd.common.Opcode;
import de.carne.mcd.common.bootstrap.InstructionReferenceEntry;

final class X86InstructionReferenceScraper extends DefaultHandler implements Iterable<X86InstructionReferenceEntry> {

	private static final Log LOG = new Log();

	private Deque<X86InstructionReferenceEntry> entries = new LinkedList<>();
	Predicate<X86InstructionReferenceEntry> filter;
	private int prefixByte = -1;
	private int twoBytePrefixByte = -1;
	private int priOpcode = -1;
	private int secOpcode = -1;
	private String mnomic = InstructionReferenceEntry.NO_VALUE;
	private String mode = InstructionReferenceEntry.NO_VALUE;
	private String procStart = InstructionReferenceEntry.NO_VALUE;
	private String procEnd = InstructionReferenceEntry.NO_VALUE;
	private final StringBuilder characterBuffer = new StringBuilder();
	private boolean characterBufferEnabled;

	X86InstructionReferenceScraper(Predicate<X86InstructionReferenceEntry> filter) {
		this.filter = filter;
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

	private void resetPriOpcode(byte opcode) {
		this.priOpcode = Byte.toUnsignedInt(opcode);
		this.procStart = "00";
		this.procEnd = InstructionReferenceEntry.NO_VALUE;
		resetEntry();
	}

	private void resetEntry() {
		this.prefixByte = -1;
		this.secOpcode = -1;
		this.mnomic = InstructionReferenceEntry.NO_VALUE;
		this.mode = "r";
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
		if ("two-byte".equals(qName)) {
			this.twoBytePrefixByte = (byte) 0x0f;
		} else if ("pri_opcd".equals(qName)) {
			startPriOpcdElement(attributes);
		} else if ("sec_opcd".equals(qName)) {
			startSecOpcdElement();
		} else if ("entry".equals(qName)) {
			startEntryElement(attributes);
		} else if ("mnem".equals(qName)) {
			startMnemElement();
		} else if ("proc_start".equals(qName)) {
			startProcStartElement();
		} else if ("proc_end".equals(qName)) {
			startProcEndElement();
		} else if ("pref".equals(qName)) {
			startPrefElement();
		}
	}

	private void startPriOpcdElement(Attributes attributes) throws SAXException {
		String opcodeString = safeGetAttribute(attributes, "value");

		LOG.debug("Processing primary opcode ''{0}''...", opcodeString);

		byte opcode = parseOpcode(opcodeString);

		resetPriOpcode(opcode);
	}

	private void startSecOpcdElement() {
		enableCharacterBuffer();
	}

	private void startEntryElement(Attributes attributes) {
		String modeAttribute = attributes.getValue("mode");

		if (modeAttribute != null) {
			this.mode = modeAttribute;
		}
	}

	private void startMnemElement() {
		enableCharacterBuffer();
	}

	private void startProcStartElement() {
		enableCharacterBuffer();
	}

	private void startProcEndElement() {
		enableCharacterBuffer();
	}

	private void startPrefElement() {
		enableCharacterBuffer();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (this.characterBufferEnabled) {
			this.characterBuffer.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("entry".equals(qName)) {
			endEntryElement();
		} else if ("sec_opcd".equals(qName)) {
			endSecOpcdElement();
		} else if ("mnem".equals(qName)) {
			endMnemElement();
		} else if ("proc_start".equals(qName)) {
			endProcStartElement();
		} else if ("proc_end".equals(qName)) {
			endProcEndElement();
		} else if ("pref".equals(qName)) {
			endPrefElement();
		}
	}

	private void endEntryElement() {
		if (!this.mnomic.equals(InstructionReferenceEntry.NO_VALUE)) {
			Opcode opcode = getOpcode();

			LOG.info("Considering opcode {0} {1}", opcode, this.mnomic);

			List<String> extraFields = new ArrayList<>();

			extraFields.add("x");
			extraFields.add("x");
			extraFields.add("x");

			X86InstructionReferenceEntry entry = new X86InstructionReferenceEntry(opcode, this.mnomic, extraFields);

			this.entries.add(entry);
		} else {
			X86InstructionReferenceEntry entry = this.entries.getLast();

		}
		resetEntry();
	}

	private void endSecOpcdElement() throws SAXException {
		String opcodeString = disableCharacterBuffer();

		this.secOpcode = Byte.toUnsignedInt(parseOpcode(opcodeString));
	}

	private void endMnemElement() {
		this.mnomic = disableCharacterBuffer();
	}

	private void endProcStartElement() {
		this.procStart = disableCharacterBuffer();
	}

	private void endProcEndElement() {
		this.procEnd = disableCharacterBuffer();
	}

	private void endPrefElement() throws SAXException {
		String prefixString = disableCharacterBuffer();

		this.prefixByte = Byte.toUnsignedInt(parseOpcode(prefixString));
	}

	@Override
	public void endDocument() throws SAXException {
		Iterator<X86InstructionReferenceEntry> entryIterator = this.entries.iterator();
		Set<Opcode> entriesOpcodes = new HashSet<>();

		while (entryIterator.hasNext()) {
			X86InstructionReferenceEntry entry = entryIterator.next();

			if (this.filter.test(entry)) {
				if (!entriesOpcodes.add(entry.opcode())) {
					LOG.warning("Duplicate opcode entry: {0}", entry);
				}
			} else {
				entryIterator.remove();
			}
		}
	}

	private Opcode getOpcode() {
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
		return Opcode.wrap(opcode, 0, opcodeLength);
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

	private static String safeGetAttribute(Attributes attributes, String qName) throws SAXException {
		String attribute = attributes.getValue(qName);

		if (attribute == null) {
			throw new SAXParseException("Missing attribute: " + qName, null);
		}
		return attribute;
	}

	@Override
	public Iterator<X86InstructionReferenceEntry> iterator() {
		return this.entries.iterator();
	}

}
