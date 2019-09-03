package com.essi.Dependency.repository;

import com.essi.dependency.Application;
import com.essi.dependency.components.Grammar;
import com.essi.dependency.repository.GrammarRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class GrammarTest {

    @Autowired
    private GrammarRepository grammarRepository;

    @Test
    public void testFetchData(){
        grammarRepository.deleteAll();
        Grammar grammar1 = new Grammar("QT", Arrays.asList("QTBUG", "QTPROJECTBUG", "QTAUTO"));
        Grammar grammar2 = new Grammar("QT2", Arrays.asList("UPC1", "UPC2", "UPC3"));

        assertNull(grammar1.getId());
        assertNull(grammar2.getId());

        grammarRepository.save(grammar1);
        grammarRepository.save(grammar2);

        assertNotNull(grammar1.getId());
        assertNotNull(grammar2.getId());
        Grammar grammar = grammarRepository.findByCompany("QT");
        assertNotNull(grammar);
        assertEquals(3, grammar.getPrefixes().size());
    }
}