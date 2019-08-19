package com.essi.dependency.components;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DependencyType {
    @JsonProperty("contributes")
    CONTRIBUTES, @JsonProperty("damages")
    DAMAGES, @JsonProperty("refines")
    REFINES, @JsonProperty("requires")
    REQUIRES, @JsonProperty("incompatible")
    INCOMPATIBLE, @JsonProperty("decomposition")
    DECOMPOSITION, @JsonProperty("similar")
    SIMILAR, @JsonProperty("duplicates")
    DUPLICATES, @JsonProperty("replaces")
    REPLACES, @JsonProperty("cross-reference")
    CROSS_REFERENCE, @JsonProperty("external-cross-reference")
    EXTERNAL_CROSS_REFERENCE;

    public DependencyType find(String elem) {
	switch (elem.toUpperCase()) {
	case "CONTRIBUTES":
	    return DependencyType.CONTRIBUTES;
	case "DAMAGES":
	    return DependencyType.DAMAGES;
	case "REFINES":
	    return DependencyType.REFINES;
	case "REQUIRES":
	    return DependencyType.REQUIRES;
	case "INCOMPATIBLE":
	    return DependencyType.INCOMPATIBLE;
	case "DECOMPOSITION":
	    return DependencyType.DECOMPOSITION;
	case "SIMILAR":
	    return DependencyType.SIMILAR;
	case "DUPLICATES":
	    return DependencyType.DUPLICATES;
	case "REPLACES":
	    return DependencyType.REPLACES;
	case "CROSS_REFERENCE":
	    return DependencyType.CROSS_REFERENCE;
	case "EXTERNAL_CROSS_REFERENCE":
	    return DependencyType.EXTERNAL_CROSS_REFERENCE;
	default:
		//error
		break;
	}
	return null;
    }

}
