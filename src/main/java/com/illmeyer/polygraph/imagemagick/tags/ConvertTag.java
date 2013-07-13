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

package com.illmeyer.polygraph.imagemagick.tags;

import java.io.IOException;
import java.io.StringWriter;

import com.illmeyer.polygraph.core.data.MessagePart;
import com.illmeyer.polygraph.imagemagick.process.MagickProcessor;
import com.illmeyer.polygraph.template.BodyExistence;
import com.illmeyer.polygraph.template.PolygraphEnvironment;
import com.illmeyer.polygraph.template.PolygraphTag;
import com.illmeyer.polygraph.template.PolygraphTemplateException;
import com.illmeyer.polygraph.template.TagInfo;
import com.illmeyer.polygraph.template.TagParameter;

@TagInfo(name = "convert", body = BodyExistence.REQUIRED, nestable = false)
public class ConvertTag implements PolygraphTag {

	@TagParameter
	private String name;

	@TagParameter
	private String codec;

	public void execute(PolygraphEnvironment env) throws IOException {
		StringWriter sw = new StringWriter();
		env.executeBody(sw);
		MessagePart part = new MessagePart();
		codec=codec.trim();
		if (!codec.matches("[0-9a-zA-Z]+"))
			throw new PolygraphTemplateException(String.format("Illegal codec '%s'",codec));
		sw.append(' ').append(codec).append(':');
		MagickProcessor proc = new MagickProcessor(MagickProcessor.getConvertExecutable(), sw.toString());
		proc.execute();
		if (proc.isError())
			throw new PolygraphTemplateException("Could execute convert: " + proc.getErrorMessage());
		part.setMessage(proc.getResult());
		env.registerMessagePart(name, part);
	}

}
