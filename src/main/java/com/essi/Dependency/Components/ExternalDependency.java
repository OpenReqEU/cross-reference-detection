package com.essi.Dependency.Components;

public class ExternalDependency {

    // private int id;
    private String externalDependency;
    private Object requirement;

    public ExternalDependency(String externalDependency, /* int id, */ Object requirmenet) {
	super();
	this.externalDependency = externalDependency;
	// this.id = id;
	this.requirement = requirmenet;
    }

    public String getExternalDependency() {
	return externalDependency;
    }

    // public void setExternalDependency(String extarnelDependency) {
    // this.extarnelDependency = extarnelDependency;
    // }
    public int referenceId() {
	return ((Clause) requirement).getId();
    }
    // public void setId(int id) {
    // this.id = id;
    // }

    public Object getRequirement() {
	return requirement;
    }

    public void setRequirement(Object requirement) {
	this.requirement = requirement;
    }

    @Override
    public String toString() {
	return ((Clause) requirement).getId() + ", External-Dependency=" + ((Clause) requirement).getClause();
    }

}
