package org.vaadin.bugrap;

import org.vaadin.bugrap.domain.BugrapRepository;

public class BaseModel {

	private static final BugrapRepository repository = new BugrapRepository(DatabaseHelper.DB_LOCATION);
	
	protected static BugrapRepository getRepository() {
		return repository;
	}
}
