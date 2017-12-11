package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.ui.columns.FineDateRenderer;

public class ThreadItemModel {
	
	private Comment comment;
	
	public ThreadItemModel(Comment c) {
		comment = c;
	}
	
	public String getAuthorText() {
		return comment.getAuthor() != null ? comment.getAuthor().getName() : "";
	}
	
	public String getLastUpdateText() {
		return FineDateRenderer.getFineTextFromDate(comment.getTimestamp());
	}
	
	public Comment getComment() {
		return comment;
	}
}
