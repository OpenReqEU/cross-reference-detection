package com.essi.dependency.functionalities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.essi.dependency.util.Control;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;

import com.essi.dependency.components.Clause;
import java.util.List;

@Repository
public class ClauseExtraction {
    private int		     currentPart    = 0;
    private int		     currentSection = 0;
    private int		     currentParag   = 0;
    private int		     clauseNumber   = 0;

    private String	     currentSubsect = "0";
    private String	     currentVolume  = null;
    private String	     currentDoc	    = null;
    private List<Object> clauseList	    = new ArrayList<>();

    private String	     part	    = Integer.toString(currentPart);
    private String	     sect	    = null;
    private String	     parag	    = null;
    private String	     subparg	    = null;
    private String	     filename;

    private String	     nextSubsection = "0";
    private String	     newSubsection  = null;

    /**
     * Constructor
     */

    public ClauseExtraction() {
	super();
    }

    private void reinizializeAtr() {
	this.currentDoc = null;
	this.currentVolume = null;
	this.currentPart = 0;
	this.currentSection = 0;
	this.currentSubsect = "0";
	this.currentParag = 0;

	this.clauseNumber = 0;
	this.clauseList = new ArrayList<>();

	this.part = Integer.toString(currentPart);
	this.sect = null;
	this.parag = null;
	this.subparg = null;

	this.nextSubsection = "0";
	this.newSubsection = null;

    }

    /**
     * Setters
     * 
     * @param filename
     */
    public void setFilename(String filename) {
	this.filename = filename;
    }

    /**
     * getters
     * 
     * @return String
     */
    public String getFilename() {
	return filename;
    }

    /**
     * Extract the HTML clauses into an Array.
     * 
     * @param path
     * @return
     * @throws IOException
     */
    public List<Object> htmlToArray(String path) throws IOException {
	reinizializeAtr();
	String p = path.replaceAll("\\|\\\\", "/");
	File input = new File(p);

	// Parse the selected document using specified charset.
	Document doc = Jsoup.parse(input, "windows-1252");

	// Get all body elements with <p> or <h*> tag.
	Elements elements = doc.body().select("p, h, h1, h2, h3, h4, h5, h6");
	String[] name = input.getName().split("\\.");
	currentDoc = name[0];

	for (Element e : elements) {
	    if (e.hasText()) {
		String line = e.text();
		extractComposition(line);
	    }
	}
	return clauseList;
    }

    /**
     * Identify the location of the clauses by analyzing them.
     * 
     * @param line
     */
    private void extractComposition(String line) {
			int nextSection = this.currentSection + 1;

			if (line.matches("^([vV][oO][lL][uU][mM][eE][\\s]?(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})[\\d]?).*")) {
				currentVolume = line.replaceAll("\\s|[vV][oO][lL][uU][mM][eE]", "");
				return;
			}
			// detect part or lot (only digits, not roman numbers!).
			if (line.matches("^([pP][aA][rR][tT][\\s]?[\\d]?).*") || line.matches("^([lL][oO][tT][\\s]?[\\d]?).*")) {
				String[] parts = line.split(" ");
				part = parts[1].replaceAll("\\D+", "");
				return;
			}

			if (line.matches("^" + nextSection + "([\\.])?([\\s])+([\\w+\\s])+[^\\.{3,}].*")) {
				currentSection = nextSection;
				nextSubsection = Integer.toString(currentSection) + ".0";
				sect = Integer.toString(currentSection);
				if (!clauseList.isEmpty()) {
					extractClauses(clauseList, line, currentDoc, currentVolume, part, sect, currentSubsect, subparg);
				}
				currentParag = 0;
				return;
			}

			// sumar numeros de dues xifres (nextSubsection)
			String tmp = nextSubsection;
			nextSubsection = nextSubsection.substring(0, nextSubsection.length() - 1)
					+ (Integer.parseInt(nextSubsection.substring(nextSubsection.length() - 1, nextSubsection.length()))
					+ 1);
			if (line.matches("^" + nextSubsection + "([\\.])?([\\s])+([\\w+\\s])+.*")) {
				currentSubsect = nextSubsection;
				newSubsection = nextSubsection + ".1";

				if (!clauseList.isEmpty()) {
					extractClauses(clauseList, line, currentDoc, currentVolume, part, sect, currentSubsect, subparg);
				}
				currentParag = 0;
				return;
			}
			nextSubsection = tmp;

			if (line.matches("^" + newSubsection + "([\\.])?([\\s])+([\\w+\\s])+.*")) {
				currentSubsect = newSubsection;
				newSubsection = newSubsection + ".1";
				nextSubsection = currentSubsect;

				if (!clauseList.isEmpty()) {
					extractClauses(clauseList, line, currentDoc, currentVolume, part, sect, currentSubsect, subparg);
				}
				currentParag = 0;
				return;

			}
			int i = 3;
			int p = 2;
			boolean find = false;
			String prevSubsection;
			String[] nexts = nextSubsection.split("\\.");
			while (!find && i < nextSubsection.length()) {

				String head = "";
				String tail = "";
				for (int j = 0; j < nexts.length; j++) {
					if (j < nexts.length - p)
						head = head.concat(nexts[j] + ".");
					else if (j < nexts.length - (p - 1))
						tail = tail.concat(Integer.parseInt(nexts[j]) + 1 + "");
				}
				prevSubsection = head + tail;

				if (line.matches("^" + (prevSubsection) + "([\\.])?([\\s])+([\\w+\\s])+.*")) {
					currentSubsect = prevSubsection;
					newSubsection = prevSubsection + ".1";
					nextSubsection = prevSubsection;

					if (!clauseList.isEmpty()) {
						extractClauses(clauseList, line, currentDoc, currentVolume, part, sect, currentSubsect,
								subparg);
					}
					currentParag = 0;
					find = true;
				}
				i += 2;
				p += 1;
			}
			if (find)
				return;

			if (currentSection != 0 && !line.equals(" ") && !line.equals("")) {
				String[] numeration = null;
				// Faltan las numeraciones romanas con parentesis
				// Hay numeraciones que no tienen ningun caracter que delimita
				// (seccion 2.4.3)!!
				// Que hago??
				if (line.matches("(((xl|l?x{0,3})(ix|iv|v?i{0,3}))|[a-zA-Z]|(\\d)+)(\\.|\\)).*")) {

					// If it uses a point as word delimiter
					if (line.matches("(((xl|l?x{0,3})(ix|iv|v?i{0,3}))|[a-zA-Z]|(\\d)+)(\\.).*")) {
						numeration = line.split("\\.");
						line = line.replaceFirst("\\.", "%"); // Replacing for a
						// random character
						// that wont be used
						// for
						// numerations.
					}
					// If it uses a parentheses
					else if (line.matches("(((xl|l?x{0,3})(ix|iv|v?i{0,3}))|[a-zA-Z]|(\\d)+)(\\)).*")) {
						numeration = line.split("\\)");
					}
					if (numeration != null) subparg = numeration[0];
				} else if (line.matches("\\(+((xl|l?x{0,3})(ix|iv|v?i{0,3}))+\\).*")) {
					numeration = line.split(" ");
					subparg = numeration[0].replaceAll("\\)|\\(", "");
					subparg = subparg.replaceAll("\\(", "");
				}
				extractClauses(clauseList, line, currentDoc, currentVolume, part, sect, currentSubsect, subparg);
				subparg = null;
			}
	}

    /**
     * Add the clauses to a list.
     * 
     * @param clauseList
     * @param line
     * @param doc
     * @param vol
     * @param part
     * @param sect
     * @param subsect
     * @param subparg
     */
    private void extractClauses(List<Object> clauseList, String line, String doc, String vol, String part,
	    String sect, String subsect, String subparg) {
	currentParag++;
	parag = Integer.toString(currentParag);
	String[] cls = line.split("\\.([\\s]+(?=[A-Z\\n])|(?=[a-z]+\\)))");
	for (String c : cls) {
	    if (!c.equals(" ")) {
		if (c.matches("^[(xl|l?x{0,3})(ix|iv|v?i{0,3})].*") || line.matches("^[a-zA-Z].*") || line.matches("^[\\d].*")) {
		    c = c.replaceFirst("%", ".");
		}
		clauseList.add(new Clause(doc, vol, part, sect, subsect, parag, subparg, c, clauseNumber));
		clauseNumber++;
	    }
	}
    }
}
