package com.essi.dependency.components;

public class ExternalDependency {

    private String externalDependency;
    private Object requirement;

    public ExternalDependency(String externalDependency, Object requirmenet) {
	super();
	this.externalDependency = externalDependency;
	this.requirement = requirmenet;
    }

    public String getExternalDependency() {
	return externalDependency;
    }

    public int referenceId() {
	return ((Clause) requirement).getId();
    }

    public Object getRequirement() {
	return requirement;
    }

    public void setRequirement(Object requirement) {
	this.requirement = requirement;
    }

    @Override
    public String toString() {
	return ((Clause) requirement).getId() + ", External-Dependency=" + ((Clause) requirement).getClauseString();
    }

}