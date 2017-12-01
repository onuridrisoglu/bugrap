package org.vaadin.bugrap.ui.columns;

import org.vaadin.bugrap.util.StringUtil;

import com.vaadin.ui.renderers.TextRenderer;

import elemental.json.JsonValue;

public class CamelcaseTextRenderer extends TextRenderer {

	@Override
	public JsonValue encode(Object value) {
		if (value == null)
			value = getNullRepresentation();
		return super.encode(StringUtil.converToCamelCaseString(value.toString()));
	}

	
}
