package com.essi.dependency.repository;

import com.essi.dependency.components.Grammar;
import org.springframework.data.repository.CrudRepository;

public interface GrammarRepository extends CrudRepository<Grammar, Integer> {

    Grammar findByCompany(String name);
    
}
