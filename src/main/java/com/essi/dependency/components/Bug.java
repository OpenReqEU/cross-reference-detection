package com.essi.dependency.components;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Bug")
public class Bug {
    private String id;
    private String summary;
    private String description;
    private List<String> comments;

    public Bug(String id, String summary, String description) {
	super();
	this.id = id;
	this.summary = summary;
	this.description = description;
	this.comments = new ArrayList<>();
    }

    public Bug(String id, String summary, String description, List<String> comments) {
        super();
        this.id = id;
        this.summary = summary;
        this.description = description;
        this.comments = comments;
    }

    public String getId() {
	return id;
    }

    public String getSummary() {
	return summary;
    }

    public String getDescription() {
	return description;
    }

    public List<String> getComments() {
        return comments;
    }

    @Override
    public String toString() {
	return "Bug [id=" + id + ",\n summary=" + summary + ",\n description=" + description
		+ "]";
    }

}
