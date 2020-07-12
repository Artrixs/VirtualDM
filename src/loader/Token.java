/*
 * Copyright (c) 2020 Arturo Misino <misino.arturo@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package loader;

public class Token {
	
	static enum TokenType {
		//Single char tokens
		LEFT_BRACE, RIGHT_BRACE, GREATER, LESS, EQUAL,
		
		//Literals
		IDENTIFIER, STRING, NUMBER, 
		
		//Keywords
		NAMESPACE, ID, TRACK, SWITCH,
		
		EOF
	}
		
	final TokenType type;
	final String lexeme;
	final Object literal;
	final int line;
	final String fileName;
	
	public Token(TokenType type, Object literal, String lexeme, int line, String fileName) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.line = line;
		this.fileName = fileName;
	}
	
	@Override
	public String toString() {
		return type + " " + literal + " at " + line;
	}
	
	
}
