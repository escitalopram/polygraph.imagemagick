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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextConsumer extends Thread {

	private BufferedReader input;
	private StringBuilder sb = new StringBuilder();

	public TextConsumer(InputStream errorStream) {
		super();
		input = new BufferedReader(new InputStreamReader(errorStream));
	}

	public void run() {
		String str;
		try {
			while ((str = input.readLine()) != null) {
				sb.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return sb.toString();
	}
}
