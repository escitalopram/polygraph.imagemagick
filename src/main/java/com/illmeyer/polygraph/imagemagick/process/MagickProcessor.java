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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class MagickProcessor {

	private static final String ConvertPathPropertyKey = "com.illmeyer.polygraph.imagemagick.convert_path";

	public static File getConvertExecutable() {
		String result = System.getProperty(ConvertPathPropertyKey);
		// TODO Make platform independent
		if (result == null)
			result = "/usr/bin/convert";
		return new File(result);
	}

	@Getter
	@Setter
	private File executable;

	@Getter
	@Setter
	private String code;

	public MagickProcessor(File executable, String code) {
		this.executable = executable;
		this.code = code;
	}

	private static final Pattern consumeWhitespace = Pattern.compile("\\s+");
	private static final Pattern consumeLiteral = Pattern.compile("[^\"\\s]+");
	static final Pattern consumeQuotedString = Pattern.compile("\"(?:\\\\\"|\\\\|[^\\\\\"])*\"");

	public void execute() throws IOException {
		List<String> args = decodeArgs(code);
		List<String> cmd = new ArrayList<String>();
		error = false;
		errorMessage = null;
		cmd.add(executable.getCanonicalPath());
		cmd.addAll(args);
		ProcessBuilder pb = new ProcessBuilder(cmd.toArray(new String[0]));
		Process p = pb.start();
		TextConsumer stderr = new TextConsumer(p.getErrorStream());
		BinaryConsumer stdout = new BinaryConsumer(p.getInputStream());
		stderr.start();
		stdout.start();
		p.getOutputStream().close();
		try {
			int result = p.waitFor();
			stderr.join();
			stdout.join();
			if (result != 0) {
				error = true;
				errorMessage = stderr.toString();
			}
			this.result = stdout.toByteArray();
		} catch (InterruptedException e) {
			error = true;
			errorMessage = String.format("%s: %s", e.getClass().getName(), e.getMessage());
		}
	}

	static List<String> decodeArgs(String source) {
		List<String> args = new ArrayList<String>();
		int position = 0;
		Matcher whiteSpace = consumeWhitespace.matcher(source);
		Matcher literal = consumeLiteral.matcher(source);
		Matcher quotedString = consumeQuotedString.matcher(source);
		whiteSpace.region(position, source.length());
		if (whiteSpace.lookingAt()) {
			position = whiteSpace.end();
		}
		while (position != source.length()) {
			StringBuilder arg = new StringBuilder();
			boolean haveArg = false;
			whiteSpace.region(position, source.length());
			while (!whiteSpace.lookingAt() && position != source.length()) {
				literal.region(position, source.length());
				if (literal.lookingAt()) {
					arg.append(literal.group());
					position = literal.end();
					haveArg = true;
				}
				quotedString.region(position, source.length());
				if (quotedString.lookingAt()) {
					String rawQS = quotedString.group();
					rawQS = rawQS.substring(1, rawQS.length() - 1);
					arg.append(rawQS.replace("\\\\", "\\").replace("\\\"", "\""));
					position = quotedString.end();
					haveArg = true;
				} else {
					if (position != source.length() && source.charAt(position) == '"')
						throw new IllegalArgumentException();
				}
				whiteSpace.region(position, source.length());
			}
			if (position != source.length())
				position = whiteSpace.end();
			if (haveArg)
				args.add(arg.toString());
		}
		return args;
	}

	@Getter
	byte[] result;

	@Getter
	String errorMessage;

	@Getter
	boolean error;
}
