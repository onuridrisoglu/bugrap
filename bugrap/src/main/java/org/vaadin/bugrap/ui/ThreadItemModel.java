package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.ui.columns.FineDateRenderer;

public class ThreadItemModel {
	
	private String author;
	private String lastUpdate;
	private String txtComment;
	
	public ThreadItemModel(Comment comment) {
		author = comment.getAuthor() != null ? comment.getAuthor().getName() : "";
		lastUpdate = FineDateRenderer.getFineTextFromDate(comment.getTimestamp());
		txtComment = comment.getComment();
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getLastUpdate() {
		return lastUpdate;
	}
	
	public String getTxtComment() {
		return txtComment;
	}
}
