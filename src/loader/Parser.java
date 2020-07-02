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
import java.util.List;

import trackinfrastructure.Layout;

/**
 * Tokenize and parse a file.
 * @author artrix
 *
 */
public class Parser {
	
	public static List<Object> parse(Path path, Layout layout) {	


		byte[] bytes;
		try {
			// For large files you should use a buffered reader!
			bytes = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Parser: IO Error reading file " + path );
		}
		List<Token> tokens = Scanner.tokenize(new String(bytes, Charset.defaultCharset()) , path.toFile().getName());
		for (Token t : tokens ) {
			System.out.println(t);
		}
		
		return null;
	}
	
public static void main(String[] args) {
	Layout layout = new Layout();
	Path path = Paths.get("test.layout");
	Parser.parse(path, layout);
}

}
