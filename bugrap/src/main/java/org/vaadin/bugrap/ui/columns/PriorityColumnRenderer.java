package org.vaadin.bugrap.ui.columns;

import org.vaadin.bugrap.domain.entities.Report.Priority;

import com.vaadin.ui.renderers.HtmlRenderer;

import elemental.json.JsonValue;

public class PriorityColumnRenderer  {

	public static String getHtmlForPriority(Priority priority) {
		int count = 0;
		switch (priority) {
			case TRIVIAL:
				count = 1;
				break;
			case NORMAL:
				count = 2;
				break;
			case MINOR:
				count = 3;
				break;
			case MAJOR:
				count = 4;
				break;
			case CRITICAL:
				count = 5;
				break;
			case BLOCKER:
				count = 6;
				break;
			}
			return generateBars(count);
	}

	private static String generateBars(int count) {
		String bar = "<div class='bar'></div>";
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; i++) {
			builder.append(bar);
		}
		return builder.toString();
	}
}
