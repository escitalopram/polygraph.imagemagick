/*
This file is part of the Polygraph bulk messaging framework
Copyright (C) 2013 Wolfgang Illmeyer

The Polygraph framework is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.illmeyer.polygraph.imagemagick.process;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestMagickProcessor {
	@Test
	public void testRegexAssertions() {
		Pattern p = Pattern.compile("a");
		Matcher m = p.matcher("aba");
		assertTrue(m.lookingAt());
		m.region(1, 3);
		assertFalse(m.lookingAt());
		m.region(2, 3);
		assertTrue(m.lookingAt());
	}

	@Test
	public void testQSRegex() {
		Matcher m = MagickProcessor.consumeQuotedString.matcher("\"karl\" \"lisa\"");
		assertTrue(m.lookingAt());
		assertEquals("\"karl\"", m.group());
	}

	@Test(timeout = 1000)
	public void testArgumentParser1() {
		List<String> result = MagickProcessor.decodeArgs(" test1 test2 ");
		assertEquals(2, result.size());
		assertEquals("test1", result.get(0));
		assertEquals("test2", result.get(1));
	}

	@Test(timeout = 1000)
	public void testArgumentParser2() {

		List<String> result = MagickProcessor.decodeArgs("test1 test2");
		assertEquals(2, result.size());
		assertEquals("test1", result.get(0));
		assertEquals("test2", result.get(1));
	}

	@Test(timeout = 1000)
	public void testArgumentParser3() {
		List<String> result = MagickProcessor.decodeArgs("\"test1\" te\"st\"2");
		assertEquals(2, result.size());
		assertEquals("test1", result.get(0));
		assertEquals("test2", result.get(1));

	}

	@Test(timeout = 1000, expected = IllegalArgumentException.class)
	public void testArgumentParser4() {
		MagickProcessor.decodeArgs("\"test1\" te\"st2");
	}

	@Test(timeout = 1000)
	public void testArgumentParser5() {
		List<String> result = MagickProcessor.decodeArgs("\"test\\\\1\" te\"st\"\\2");
		assertEquals(2, result.size());
		assertEquals("test\\1", result.get(0));
		assertEquals("test\\2", result.get(1));

	}

	@Test(timeout = 1000)
	public void testArgumentParser6() {
		List<String> result = MagickProcessor.decodeArgs("\"test\\\"1\" te\"st\"\"\\\"\"2");
		assertEquals(2, result.size());
		assertEquals("test\"1", result.get(0));
		assertEquals("test\"2", result.get(1));

	}

	@Test(timeout = 1000)
	public void testArgumentParser7() {
		List<String> result = MagickProcessor.decodeArgs("\"\\\\\\\"\"");
		assertEquals(1, result.size());
		assertEquals("\\\"", result.get(0));

	}

	@Test
	public void testCreateImage() throws IOException {
		MagickProcessor p = new MagickProcessor(new File("/usr/bin/convert"), "-size 1x1 canvas:none gif:-");
		p.execute();
		assertFalse(p.isError());
		byte[] result = p.getResult();
		assertNotNull(result);
		assertTrue(result.length > 3);
		assertTrue(result[0] == 'G');
		assertTrue(result[1] == 'I');
		assertTrue(result[2] == 'F');
	}
}
