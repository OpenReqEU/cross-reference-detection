package com.essi.Dependency.Service;

import com.essi.Dependency.Components.Grammar;
import com.essi.Dependency.Repository.GrammarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrammarService {

    @Autowired
    private GrammarRepository grammarRepository;

    public Grammar findByCompany(String company) {
        return grammarRepository.findByCompany(company);
    }

    public void uploadGrammar(String company, Grammar grammar) throws Exception {
        Grammar grammar1 = grammarRepository.findByCompany(company);
        if (grammar1 != null)
            throw new Exception("Company grammar already exists");

        grammar.setCompany(company);
        grammarRepository.save(grammar);
    }

    public void updateGrammar(String company, Grammar grammar) throws Exception{
        Grammar grammar1 = grammarRepository.findByCompany(company);
        if (grammar1 == null)
            throw new Exception("Company grammar does not exist");

        grammar.setCompany(company);
        grammar.setId(grammar1.getId());
        grammarRepository.save(grammar);
    }

    public void deleteGrammar(String company) {
        Grammar grammar = grammarRepository.findByCompany(company);
        grammarRepository.delete(grammar.getId());
    }

    public Grammar getGrammar(String company) throws Exception {
        Grammar grammar = grammarRepository.findByCompany(company);
        if (grammar == null)
            throw new Exception("Company grammar does not exist");

        return grammar;
    }
}
