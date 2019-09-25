package com.essi.dependency.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "grammar")
public class Grammar implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Integer id;
    @Column(unique=true)
    @JsonIgnore
    private String company;
    private String prefixes;

    public Grammar(String company, List<String> prefixes) {
        this.company = company;
        this.prefixes = prefixes.stream().collect(Collectors.joining(","));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<String> getPrefixes() {
        return Arrays.asList(prefixes.split(","));
    }

    public void setPrefixes(List<String> prefixes) {
        this.prefixes = prefixes.stream().collect(Collectors.joining(","));
    }
}
