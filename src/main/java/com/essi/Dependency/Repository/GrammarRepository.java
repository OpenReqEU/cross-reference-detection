package com.essi.Dependency.Repository;

import com.essi.Dependency.Components.Grammar;
import org.springframework.data.repository.CrudRepository;

public interface GrammarRepository extends CrudRepository<Grammar, Integer> {

    Grammar findByCompany(String name);
    
}
