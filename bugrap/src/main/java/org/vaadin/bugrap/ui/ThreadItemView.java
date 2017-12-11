package org.vaadin.bugrap.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.vaadin.bugrap.domain.entities.Comment.Type;
import org.vaadin.bugrap.ui.generated.ThreadItemBase;

import com.vaadin.server.StreamResource;

public class ThreadItemView extends ThreadItemBase {
	
	private ThreadItemModel model;
	
	public ThreadItemView(ThreadItemModel tim) {
		model = tim;
		init();
	}

	private void init() {
		lblAuthor.setCaption(String.format("%s (%s)", model.getAuthorText(), model.getLastUpdateText()));
		if (model.getComment().getType() == Type.COMMENT) {
			lblComment.setValue(model.getComment().getComment());
			lblComment.setVisible(true);
			lnkAttachment.setVisible(false);
		} else if (model.getComment().getType() == Type.ATTACHMENT) {
			lblComment.setVisible(false);
			lnkAttachment.setVisible(true);
			lnkAttachment.setCaption(model.getComment().getAttachmentName());
			lnkAttachment.setResource(new StreamResource(new StreamResource.StreamSource() {
				@Override
				public InputStream getStream() {
					return new ByteArrayInputStream(model.getComment().getAttachment());
				}
			}, model.getComment().getAttachmentName()));
		}

	}
}
