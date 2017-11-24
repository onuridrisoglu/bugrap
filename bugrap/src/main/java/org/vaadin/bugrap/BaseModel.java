package org.vaadin.bugrap;

import org.vaadin.bugrap.domain.BugrapRepository;

public class BaseModel {

	private static final BugrapRepository _repository = new BugrapRepository(DatabaseHelper.DB_LOCATION);
	
	protected static BugrapRepository getRepository() {
		return _repository;
	}
}
