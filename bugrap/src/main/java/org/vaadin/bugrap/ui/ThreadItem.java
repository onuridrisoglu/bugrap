package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.ui.columns.FineDateRenderer;
import org.vaadin.bugrap.ui.generated.ThreadItemBase;

public class ThreadItem extends ThreadItemBase {

	public ThreadItem(Comment comment) {
		String author = comment.getAuthor() != null ? comment.getAuthor().getName() : "";
		String lastUpdate = FineDateRenderer.getFineTextFromDate(comment.getTimestamp());
		lblAuthor.setCaption(String.format("%s (%s)", author, lastUpdate));
		lblComment.setValue(comment.getComment());
	}
}
