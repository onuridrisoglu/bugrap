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
		StringBuilder builder = new StringBuilder();
		for (String word : words) {
			if (word.length() == 0)
				continue;
			String firstChar = word.substring(0, 1).toUpperCase();
			String rest = word.substring(1).toLowerCase();
			if (builder.length() > 0)
				builder.append(" ");
			builder.append(firstChar).append(rest);
		}
		return builder.toString();
	}
}
