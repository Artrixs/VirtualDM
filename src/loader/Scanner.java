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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loader.Token.TokenType;

public class Scanner {
	
	private static List<Token> tokens;
	private static String source;
	private static int start;
	private static int current;
	private static int line;
	private static String fileName;
	
	private static Map<String, TokenType> keywords = new HashMap();
	
	static { 
		keywords.put("namespace",	TokenType.NAMESPACE);
		keywords.put("Track",		TokenType.TRACK);
		keywords.put("Switch", 		TokenType.SWITCH);
		keywords.put("id", TokenType.ID);
	}
	
	public static List<Token> tokenize(String fileSource, String name) {
		tokens = new ArrayList<Token>();
		source = fileSource;
		fileName = name;
		start = 0;
		current = 0;
		line = 1;
		
		while ( !isEnd() ) {
			start = current;
			scanToken();
		}
		
		tokens.add( new Token(TokenType.EOF, null, "", line, fileName) );
		
		return tokens;
	}
	
	private static void scanToken() {
		char c = advance();
		
		switch (c) {
		case '{': addToken(TokenType.LEFT_BRACE); break;
		case '}': addToken(TokenType.RIGHT_BRACE); break;
		case '<': addToken(TokenType.LESS); break;
		case '>': addToken(TokenType.GREATER); break;
		case '=': addToken(TokenType.EQUAL); break;
		
		case ' ':
		case '\r':
		case '\t':
			break;
		case '\n':
			line++;
			break;
		
		case '"': string(); break;
		
		default:
			if ( isDigit(c) ) {
				number();
			} else if ( isAlpha(c) ) {
				identifier();
			} else {
				throw new RuntimeException("Parser: Unexcepted charachter '" + c + "' at " + fileName + ":" + line);
			}
		}
		return;
	}
	
	private static void string() {
		while ( !isEnd() && peek() != '"' ) {
			if ( peek() == '\n') {
				line++;
			}
			advance();
		}
		
		if ( isEnd() ) throw new RuntimeException("Parser: Unterminated string at " + fileName + ":" + line);
		
		advance();
		
		String text = source.substring(start + 1, current - 1);
		addToken(TokenType.STRING, text);
	}
	
	private static void number() {
		boolean isDouble = false;
		while ( isDigit(peek() ) ) {
			advance();
		}
		
		//Float number
		if ( peek() == '.' && isDigit(peekNext() ) ) {
			isDouble = true;
			advance();
		}
		
		while (  isDigit(peek() ) ) {
			advance();
		}
		
		String text = source.substring(start, current);
		addToken(TokenType.NUMBER, isDouble ? Double.parseDouble(text) : Integer.parseInt(text ) );
	}
	
	private static void identifier() {
		while ( isAlphaNumeric( peek() ) ) {
			advance();
		}
		
		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		
		if ( type == null ) type = TokenType.IDENTIFIER;
		addToken(type, text);
	}

	
	private static void addToken(TokenType type) {
		addToken(type, null);
	}
	
	private static void addToken(TokenType type, Object litteral) {
		String lexeme = source.substring(start, current);
		tokens.add( new Token(type, litteral, lexeme, line, fileName) );
	}
	
	private static boolean match( char expected ) {
		if ( isEnd() ) return false;
		if ( expected != source.charAt(current) ) {
			return false;
		}
		current++;
		return true;
	}
	
	private static char advance() {
		if ( isEnd() ) return '\0';
		current++;
		return source.charAt(current - 1);
	}
	
	private static char peek() {
		if ( isEnd() ) return '\0';
		return source.charAt( current );
	}
	
	private static boolean isDigit(char c) {
		return c >= '0' && c<= '9';
	}
	
	private static boolean isAlpha(char c) {
		return ( c >= 'a' && c <= 'z' ) ||
			   ( c >= 'A' && c <= 'Z' ) ||
			   c == '_' ;
	}
	
	private static boolean isAlphaNumeric( char c ) {
		return isDigit(c) || isAlpha(c);
	}
	
	private static char peekNext() {
		if (current > source.length() - 1 ) return '\0';
		return source.charAt( current + 1);
	}
	
	private static boolean isEnd() {
		return current >= source.length();
	}

}
