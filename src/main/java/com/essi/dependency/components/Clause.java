package com.essi.dependency.components;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Clause")
public class Clause {

    private String doc;
    private String vol;
    private String part;
    private String sect;
    private String subsect;
    private String parag;
    private String subparg;
    private String clauseString;
    private int	   id;

    public Clause(String doc, String vol, String part, String sect, String subsect, String parag, String subparg,
                  String clauseString, int id) {
	super();
	this.doc = doc;
	this.vol = vol;
	this.part = part;
	this.sect = sect;
	this.subsect = subsect;
	this.parag = parag;
	this.subparg = subparg;
	this.clauseString = clauseString;
	this.id = id;
    }

    public Clause(List<String> components) {
	this.id = Integer.parseInt(components.get(0));
	this.clauseString = components.get(1);
	this.doc = components.get(2);
	this.vol = components.get(3);
	this.part = components.get(4);
	this.sect = components.get(5);
	this.subsect = components.get(6);
	this.parag = components.get(7);
	this.subparg = components.get(8);
    }

    @ApiModelProperty(value = "Document")
    public String getDoc() {
	return doc;
    }

    @ApiModelProperty(value = "Volume")
    public String getVol() {
	return vol;
    }

    @ApiModelProperty(value = "Part")
    public String getPart() {
	return part;
    }

    @ApiModelProperty(value = "Section")
    public String getSect() {
	return sect;
    }

    @ApiModelProperty(value = "Subsection")
    public String getSubsect() {
	return subsect;
    }

    @ApiModelProperty(value = "Paragraph")
    public String getParag() {
	return parag;
    }

    @ApiModelProperty(value = "Subparagraph")
    public String getSubparg() {
	return subparg;
    }

    @ApiModelProperty(value = "Text")
    public String getClauseString() {
	return clauseString;
    }

    @ApiModelProperty(value = "Identification number")
    public int getId() {
	return id;
    }

    public void setClauseString(String clauseString) {
	this.clauseString = clauseString;
    }

    public String printMe() {
	return this.clauseString;
    }

    @Override
    public String toString() {
	return "Clause [id=" + id + ",\n doc=" + doc + ",\n vol=" + vol + ",\n part=" + part + ",\n sect=" + sect
		+ ",\n subsect=" + subsect + ",\n parag=" + parag + ",\n subparg=" + subparg + ",\n clauseString=" + clauseString
		+ "]";
    }

}
