package com.essi.Dependency.Components;

import java.util.ArrayList;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Bug")
public class Bug {
    private int	   id;
    private String summary;
    private String description;

    public Bug(int id, String summary, String description) {
	super();
	this.id = id;
	this.summary = summary;
	this.description = description;
    }

    public Bug(ArrayList<String> components) {
	this.id = Integer.parseInt(components.get(0));
	this.summary = components.get(1);
	this.description = components.get(2);
    }

    public int getId() {
	return id;
    }

    public String getSummary() {
	return summary;
    }

    public String getDescription() {
	return description;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return "Bug [id=" + id + ",\n summary=" + summary + ",\n description=" + description
		+ "]";
    }

}
