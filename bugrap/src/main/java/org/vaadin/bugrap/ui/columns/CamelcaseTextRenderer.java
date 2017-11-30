package org.vaadin.bugrap.ui.columns;

import com.vaadin.ui.renderers.TextRenderer;

import elemental.json.JsonValue;

public class CamelcaseTextRenderer extends TextRenderer {

	@Override
	public JsonValue encode(Object value) {
		if (value == null)
			value = getNullRepresentation();
		return super.encode(converToCamelCaseString(value.toString()));
	}

	private String converToCamelCaseString(String str) {
		String[] words = str.split(" ");
		StringBuffer buffer = new StringBuffer();
		for (String word : words) {
			if (word.length() == 0)
				continue;
			String firstChar = word.substring(0, 1);
			firstChar = firstChar.toUpperCase();
			String rest = word.substring(1);
			rest = rest.toLowerCase();
			if (buffer.length() > 0)
				buffer.append(" ");
			buffer.append(firstChar + rest);
		}
		return buffer.toString();
	}
}
