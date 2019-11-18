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

}
