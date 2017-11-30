package org.vaadin.bugrap.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.vaadin.bugrap.BaseModel;
import org.vaadin.bugrap.domain.BugrapRepository.ReportsQuery;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.vaadin.data.Binder;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;

public class ReportsModel extends BaseModel{

	private Project selectedProject;
	private ProjectVersion selectedVersion;
	
	public ReportsModel(Navigator navigator) {
		super(navigator);
	}
	
	
	public void fillProjects(ComboBox<Project> combo) {
		List<Project> projects = new ArrayList<Project>();
		projects.addAll(getRepository().findProjects());
		Collections.sort(projects);
		combo.setItems(projects);
		combo.setSelectedItem(projects.get(0));
	}

	public void processProjectChange(Optional<Project> selectedItem, ComboBox<ProjectVersion> cmbVersion) {
		selectedProject = selectedItem.get();
		List<ProjectVersion> projectVersions = new ArrayList<ProjectVersion>();
		projectVersions.addAll(getRepository().findProjectVersions(selectedProject));
		Collections.sort(projectVersions);
		
		cmbVersion.setItems(projectVersions);
		cmbVersion.setSelectedItem(projectVersions.get(0));
	}

	public void processVersionChange(Optional<ProjectVersion> selectedItem, Grid<Report> gridReports) {
		selectedVersion = selectedItem.get();
		
		ReportsQuery query = new ReportsQuery();
		query.project = selectedProject;
		query.projectVersion = selectedVersion;
		
		List<Report> reports = new ArrayList<Report>();
		reports.addAll(getRepository().findReports(query));
		gridReports.setItems(reports);
	}

}
