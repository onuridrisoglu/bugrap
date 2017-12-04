package org.vaadin.bugrap;

import org.vaadin.bugrap.domain.BugrapRepository;

public class DatabaseHelper {
	
	public static final String DB_LOCATION = "/Users/onuridrisoglu/Development/eclipse-workspace/bugrap-data/bugrap";
	
	/**
	 * Checks whether the DB is empty or not
	 * @param repository
	 * @return true if DB was empty but initialized
	 */
	public static boolean initializeIfEmpty(BugrapRepository repository) {
		if (repository.findReporters().size() > 0)
			return false;
		return repository.populateWithTestData();
	}
}
