/*
 * Copyright (c) 2019-2021 Holger de Carne and contributors, All Rights Reserved.
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
grammar Decl;

@header {
package de.carne.mcd.jvmdecoder.classfile.decl.grammar;
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

VoidType: 'V';
ObjectType: 'L';
TypeType: 'T';
AnyType: '*';

LBracket: '(';
RBracket: ')';
LSBracket: '[';
LABracket: '<';
RABracket: '>';
Slash: '/';
Plus: '+';
Minus: '-';
Comma: ',';
Colon: ':';
Semicolon: ';';
FullStop: '.';
Caret: '^';

Any: .;

// Rules

fieldDescriptor
	: descriptorType
	;

descriptorType
	: (baseType|objectType|arrayType)
	;	

baseType
	: (BaseTypeB|BaseTypeC|BaseTypeD|BaseTypeF|BaseTypeI|BaseTypeJ|BaseTypeS|BaseTypeZ|VoidType)
	;

objectType
	: ObjectType className Semicolon
	;

className
	: ~(Semicolon)+
	;

arrayType
	: LSBracket componentType
	;

componentType
	: descriptorType
	;

methodDescriptor
	: LBracket parameterDescriptor* RBracket returnDescriptor
	;

parameterDescriptor
	: descriptorType
	;
	
returnDescriptor
	: descriptorType
	;

javaTypeSignature
	: (baseType|referenceTypeSignature)
	;
	
referenceTypeSignature
	: (classTypeSignature|typeVariableSignature|arrayTypeSignature)
	;
	
classTypeSignature
	: ObjectType classTypeName typeArguments? classTypeSignatureSuffix* Semicolon
	;

classTypeName
	: packageSpecifier? identifier
	;
	
packageSpecifier
	: identifier Slash packageSpecifier* 
	;

identifier
	: ~(FullStop|Semicolon|Colon|Slash|LSBracket|LABracket|RABracket)+
	;
	
typeArguments
	: LABracket typeArgument+ RABracket
	;
	
typeArgument
	: (wildcardIndicator? referenceTypeSignature|AnyType)
	;

wildcardIndicator
	: (Plus|Minus)
	;
	
classTypeSignatureSuffix
	: FullStop identifier typeArguments?
	;
	
typeVariableSignature
	: TypeType identifier Semicolon
	;

arrayTypeSignature
	: LSBracket javaTypeSignature
	;

classSignature
	: typeParameters? superClassSignature superInterfaceSignature*
	;
	
typeParameters
	: LABracket typeParameter+ RABracket
	;
	
typeParameter
	: identifier classBound interfaceBound*
	;
	
classBound
	: Colon referenceTypeSignature?
	;

interfaceBound
	: Colon referenceTypeSignature
	;

superClassSignature
	: referenceTypeSignature
	;

superInterfaceSignature
	: referenceTypeSignature
	;

methodSignature
	: typeParameters? LBracket javaTypeSignature* RBracket returnType throwsSignature*
	;
	
returnType
	: javaTypeSignature
	;

throwsSignature
	: Caret(classTypeSignature|typeVariableSignature)
	;

fieldSignature
	: referenceTypeSignature
	;
