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

package com.illmeyer.polygraph.imagemagick;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.illmeyer.polygraph.core.data.DependencySpecification;
import com.illmeyer.polygraph.core.data.VersionNumber;
import com.illmeyer.polygraph.core.spi.Extension;
import com.illmeyer.polygraph.imagemagick.tags.ConvertTag;
import com.illmeyer.polygraph.template.DefaultTagFactory;
import com.illmeyer.polygraph.template.TagAdapter;

public class ImageMagick implements Extension {

	public Map<String, Object> createContext() {
		Map<String, Object> result = new HashMap<String, Object>();
		new TagAdapter(new DefaultTagFactory(ConvertTag.class)).register(result);
		return result;
	}

	public VersionNumber getVersionNumber() {
		return new VersionNumber(0, 0, 1);
	}

	public void initialize() {
	}

	public void destroy() {
	}

	public List<DependencySpecification> getRequiredExtensions() {
		return Collections.emptyList();
	}

}
