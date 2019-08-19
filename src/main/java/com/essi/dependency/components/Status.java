package com.essi.dependency.components;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty("proposed")
    PROPOSED,
    @JsonProperty("accepted")
    ACCEPTED,
    @JsonProperty("rejected")
    REJECTED
}
