package com.essi.dependency.service;

import com.essi.dependency.components.Grammar;
import com.essi.dependency.exception.InternalErrorException;
import com.essi.dependency.repository.GrammarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrammarService {

    @Autowired
    private GrammarRepository grammarRepository;

    public Grammar findByCompany(String company) {
        return grammarRepository.findByCompany(company);
    }

    public void uploadGrammar(String company, Grammar grammar) throws InternalErrorException {
        Grammar grammar1 = grammarRepository.findByCompany(company);
        if (grammar1 != null)
            throw new InternalErrorException("Company grammar already exists");

        grammar.setCompany(company);
        grammarRepository.save(grammar);
    }

    public void updateGrammar(String company, Grammar grammar) throws InternalErrorException {
        Grammar grammar1 = grammarRepository.findByCompany(company);
        if (grammar1 == null)
            throw new InternalErrorException("Company grammar does not exist");

        grammar.setCompany(company);
        grammar.setId(grammar1.getId());
        grammarRepository.save(grammar);
    }

    public void deleteGrammar(String company) {
        Grammar grammar = grammarRepository.findByCompany(company);
        grammarRepository.delete(grammar.getId());
    }

    public Grammar getGrammar(String company) throws InternalErrorException {
        Grammar grammar = grammarRepository.findByCompany(company);
        if (grammar == null)
            throw new InternalErrorException("Company grammar does not exist");

        return grammar;
    }
}
