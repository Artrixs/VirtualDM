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


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import trackinfrastructure.Layout;
import trackinfrastructure.trackelements.Switch;
import trackinfrastructure.trackelements.Track;
import utils.ID;
import loader.Token.TokenType;

/**
 * Tokenize and parse a file.
 * @author artrix
 *
 */
public class Parser {
	
	private static int current;
	private static List<Token> tokens;
	private static List<LoaderElement> loaderElements;
	private static Layout layout;
	private static String inNamespace;
	
	public static List<LoaderElement> parse(Path path, Layout passedLayout) {	
		layout = passedLayout;
		
		byte[] bytes;
		try {
			// For large files you should use a buffered reader!
			bytes = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Parser: IO Error reading file " + path );
		}
		//Tokenize file
		tokens = Scanner.tokenize(new String(bytes, Charset.defaultCharset()) , path.toFile().getName());
		
		
		return parseLayout(layout);
	}
	
	private static List<LoaderElement> parseLayout(Layout layout) {
		loaderElements = new ArrayList<LoaderElement>();
		inNamespace = null;
		
		while( !isEnd() ) {
			namespace();
		}
		return loaderElements;
	}
	
	private static void namespace() {
		if ( match(TokenType.NAMESPACE) ) {
			inNamespace = consume(TokenType.IDENTIFIER, "Missing namespcae name").lexeme;
			consume(TokenType.LEFT_BRACE, "Expected { after namespace");
		}
		
		while ( match(Token.TokenType.LESS) ) {
			statement();
		}
		
		if ( inNamespace != null ) {
			consume(TokenType.RIGHT_BRACE, "Expected } to close namespace");
		}
		inNamespace = null;
	}
	
	private static void statement() {
		Token t = advance();
		switch (t.type) {
		case TRACK:
			track();
			break;
		case SWITCH:
			switch_();
			break;
		default:
			throw new RuntimeException("Unrecognized type element");
		}
		
		consume(TokenType.GREATER, "Expeced > after a statement");
	}
	
	private static void track() {
		ID id = parseID();
		
		var loaderElement = new LoaderElement(id, LoaderElement.Type.TRACK);
		while( check(TokenType.IDENTIFIER) ) {
			parseAttribute(loaderElement);
		}
		
		if( !loaderElement.hasAttribute("length") ) {
			throw new RuntimeException("Track elemenet must have a lenght!");
		}
		double length = (double) loaderElement.getAttribute("length");
		
		layout.addTrackElement(id, new Track(id, length));
		loaderElements.add(loaderElement);
	}
	
	private static void switch_() {
		var loaderElement = parseTrackElement(LoaderElement.Type.SWITCH, "lengthA", "lengthB", "lengthC");
		var id = loaderElement.getID();
		
		double lengthA = (double) loaderElement.getAttribute("lengthA");
		double lengthB = (double) loaderElement.getAttribute("lengthB");
		double lengthC = (double) loaderElement.getAttribute("lengthC");
		
		layout.addTrackElement(id, new Switch(id, lengthA, lengthB, lengthC));
		loaderElements.add(loaderElement);		
	}
	
	private static LoaderElement parseTrackElement(LoaderElement.Type type, String... requiredAttributes) {
		ID id = parseID();
		LoaderElement loaderElement = new LoaderElement(id, type);
		
		while ( check( TokenType.IDENTIFIER ) ) {
			parseAttribute( loaderElement );
		}
		
		String name = loaderElement.hasAttributes(requiredAttributes);
		if ( name != null ) 
			throw new RuntimeException("Must have a " + name);
		return loaderElement;
	}
	
	private static ID parseID() {
		consume(TokenType.ID, "Expected an ID for this statement");
		consume(TokenType.EQUAL, "Expected = after id");
		String name = (String) consume(TokenType.STRING, "Expceted a name for id").literal;
		if (name.contains(":") ) throw new RuntimeException("ID name can not contain the charachter ':'");
	
		if( inNamespace != null) {
			return new ID(inNamespace, name);
		}
			
		return new ID(name);
	}
	
	private static void parseAttribute(LoaderElement loaderElement) {
		String name = consume(TokenType.IDENTIFIER, "Expected a identifier").lexeme;
		consume(TokenType.EQUAL, "Expected a equal sign");
		if ( !check(TokenType.STRING, TokenType.NUMBER) ) {
			throw new RuntimeException("Expected a string or a number after =");
		}
		
		loaderElement.setAttribute(name, advance().literal);		
	}
	
	private static boolean match(TokenType type) {
		if ( isEnd() ) return false;
		if ( type != tokens.get( current ).type ) return false;
		
		current++;
		return true;
	}
	
	private static Token advance() {
		if ( isEnd() ) return tokens.get( tokens.size() );
		current++;
		return tokens.get( current - 1 );
	}
	
	private static Token consume(TokenType type, String errorText) {
		if ( check( type ) ) return advance();
		
		throw new RuntimeException(errorText);
	}
	
	private static Token peek() {
		if ( isEnd() ) return tokens.get( tokens.size() );
		return tokens.get( current );
	}
	
	private static boolean check(TokenType... types) {
		if( isEnd() ) return false;
		for( TokenType t : types) {
			if (tokens.get(current).type == t)
				return true;
		}
		return false;
	}
	
	private static boolean isEnd() {
		return current >= tokens.size() || tokens.get(current).type == TokenType.EOF;
	}
	
}
