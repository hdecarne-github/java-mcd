/*
 * Copyright (c) 2019 Holger de Carne and contributors, All Rights Reserved.
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
grammar DescriptorGrammar;

@header {
package de.carne.mcd.jvm.decode.descriptor.grammar;
}

// Tokens

BaseTypeB: 'B';
BaseTypeC: 'C';
BaseTypeD: 'D';
BaseTypeF: 'F';
BaseTypeI: 'I';
BaseTypeJ: 'J';
BaseTypeS: 'S';
BaseTypeZ: 'Z';

ObjectType: 'L';

VoidType: 'V';

AnyType: '*';

LBracket: '(';
RBracket: ')';
LSBracket: '[';
LABracket: '<';
RABracket: '>';
Comma: ',';
Colon: ':';
Semicolon: ';';

Any: .;

// Rules

fieldDescriptor
	: fieldType
	;

fieldType
	: (integralType|objectType|arrayType)
	;	

integralType
	: (BaseTypeB|BaseTypeC|BaseTypeD|BaseTypeF|BaseTypeI|BaseTypeJ|BaseTypeS|BaseTypeZ|VoidType)
	;

objectType
	: ObjectType identifier Semicolon
	;

identifier
	: ~(Semicolon|Colon|LABracket)+
	;

arrayType
	: LSBracket componentType
	;

componentType
	: fieldType
	;

methodDescriptor
	: LBracket parameterDescriptor* RBracket returnDescriptor
	;

parameterDescriptor
	: fieldType
	;
	
returnDescriptor
	: fieldType
	;
