/*
 * Copyright (C) 2007-2012 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.formats;

import java.util.*;

import org.geometerplus.zlibrary.core.filesystem.ZLResourceFile;
import org.geometerplus.zlibrary.core.xml.ZLStringMap;
import org.geometerplus.zlibrary.core.xml.ZLXMLReaderAdapter;

public final class JavaEncodingCollection extends EncodingCollection {
	private final List<Encoding> myEncodings = new ArrayList<Encoding>();
	private final Map<String,Encoding> myEncodingByAlias = new HashMap<String,Encoding>();

	public JavaEncodingCollection() {
		new EncodingCollectionReader().readQuietly(
			ZLResourceFile.createResourceFile("encodings/Encodings.xml")
		);
	}
	
	@Override
	public List<Encoding> encodings() {
		return Collections.unmodifiableList(myEncodings);
	}

	@Override
	public Encoding getEncoding(String alias) {
		final Encoding e = myEncodingByAlias.get(alias);
		return e != null ? e : new Encoding(null, alias, alias);
	}

	@Override
	public Encoding getEncoding(int code) {
		return getEncoding(String.valueOf(code));
	}

	private class EncodingCollectionReader extends ZLXMLReaderAdapter {
		private String myCurrentFamilyName;
		private Encoding myCurrentEncoding;

		public boolean dontCacheAttributeValues() {
			return true;
		}

		public boolean startElementHandler(String tag, ZLStringMap attributes) {
			if ("group".equals(tag)) {
				myCurrentFamilyName = attributes.getValue("name");
			} else if ("encoding".equals(tag)) {
				final String name = attributes.getValue("name");
				final String region = attributes.getValue("region");
				myCurrentEncoding = new Encoding(
					myCurrentFamilyName, name, name + " (" + region + ")"
				);
				myEncodings.add(myCurrentEncoding);
				myEncodingByAlias.put(name, myCurrentEncoding);
			} else if ("code".equals(tag)) {
				myEncodingByAlias.put(attributes.getValue("number"), myCurrentEncoding);
			} else if ("alias".equals(tag)) {
				myEncodingByAlias.put(attributes.getValue("name"), myCurrentEncoding);
			}
			return false;
		}
	}
}
