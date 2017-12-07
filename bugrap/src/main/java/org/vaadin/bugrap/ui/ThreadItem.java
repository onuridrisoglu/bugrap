package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.ui.columns.FineDateRenderer;
import org.vaadin.bugrap.ui.generated.ThreadItemBase;

public class ThreadItem extends ThreadItemBase {

	public ThreadItem(Comment comment) {
		lblAuther.setCaption(comment.getAuthor() + " (" + FineDateRenderer.getFineTextFromDate(comment.getTimestamp()) + ")");
		lblComment.setValue(comment.getComment());
	}
}
