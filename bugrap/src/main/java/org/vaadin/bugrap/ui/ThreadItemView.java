package org.vaadin.bugrap.ui;

import org.vaadin.bugrap.ui.generated.ThreadItemBase;

public class ThreadItemView extends ThreadItemBase {
	
	private ThreadItemModel model;
	
	public ThreadItemView(ThreadItemModel tim) {
		model = tim;
		lblAuthor.setCaption(String.format("%s (%s)", model.getAuthor(), model.getLastUpdate()));
		lblComment.setValue(model.getTxtComment());
	}
}
