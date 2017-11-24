package org.vaadin.bugrap;

import org.vaadin.bugrap.domain.BugrapRepository;

public class DatabaseHelper {
	
	public static final String DB_LOCATION = "/Users/onuridrisoglu/Development/eclipse-workspace/bugrap-data/bugrap";
	
	public static void main(String[] args) {
		BugrapRepository repository = new BugrapRepository(DB_LOCATION);
		repository.populateWithTestData();
		
	}
}
