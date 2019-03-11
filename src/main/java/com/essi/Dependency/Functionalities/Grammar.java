package com.essi.Dependency.Functionalities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Repository;

import com.essi.Dependency.Components.Bug;
import com.essi.Dependency.Components.Clause;
import com.essi.Dependency.Components.Dependency;
import com.essi.Dependency.Components.DependencyType;
import com.essi.Dependency.Components.ExternalDependency;
import com.essi.Dependency.Components.Status;

@Repository
public class Grammar {

	// Gazetters
	private String linkTerm;
	private String implicitTerm;
	private String markerTerm;
	private String sepTerm;
	private String externalTerm;

	// expresions
	private String numExp;
	private String valorExp;
	private String ordinalExp;

	// Simple Grammar
	private String internalExp;
	private String implicitExp;
	private String implicitComplexExp;
	private String simpleExp;

	// Complex Grammar
	private String multiValuedExp;
	private String multiLayeredExp;
	private String externalExp;
	private String complexExp;

	private String grammar;
	private String[] bugPrefixs;

	/**
	 * Constructor
	 */
	public Grammar() {
		super();
		// Gazetteers
		this.linkTerm = "((\\s)?of(\\s)?|(\\s)?of the(\\s)?|(\\s)?of a(\\s)?)";

		this.implicitTerm = "((\\s)?above(\\s)?|(\\s)?below(\\s)?|(\\s)?preceding(\\s)?|(\\s)?following(\\s)?|(\\s)?that follows(\\s)?|"
				+ "(\\s)?next(\\s)?|(\\s)?previous(\\s)?|(\\s)?this(\\s)?|(\\s)?same(\\s)?|(\\s)?current(\\s)?)";
		this.markerTerm = "((\\s+|\\(|\\|)(section(s)?(\\s)?|sect\\.(\\s)?|subsection(s)?(\\s)?|subsect\\.(\\s)?|paragraph(s)?(\\s)?|"
				+ "subparagraph(s)?(\\s)?|subitem(s)?(\\s)?|volume(s)?(\\s)?|lot('s)?(\\s)?|book(s)?(\\s)?"
				+ "|article(s)?(\\s)?|item(s)?(\\s)?|point(s)?(\\s)?" +
                "|" +
                "(\\s)?qt(-)?bug(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?3ds(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?jira(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?creatorbug(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?ifw(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?mobility(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?playground(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?website(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?qainfra(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?components(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?solbug(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?vsaddinbug(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?wb(s(:?))?(-)?" +
                "|" +
                "(\\s)?qt(-)?sysadm(s(:?))?(-)?" +
                "|" +
                "[?]id(=)?" +
                "|" +
                "(\\s)?bug(s(:?))?(\\s)?(#)?" +
                "|" +
                "(\\s)?qbs(s(:?))?(\\s)?(#)?" +
                "|" +
                "(\\s)?autosuite(s(:?))?(\\s)?(#)?" +
                "|" +
                "(\\s)?qds(s(:?))?(\\s)?(#)?" +
                "|" +
                "(\\s)?pyside(s(:?))?(\\s)?(#)?" +
                "|" +
                "(\\s)?qsr(s(:?))?(\\s)?(#)?" +
                "))";
		this.sepTerm = "(,(\\s)?|(\\s)?-(\\s)?|(\\s)?and(\\s)?|(\\s)?or(\\s)?|(\\s)?to(\\s)?|(\\s)?in(\\s)?)";
		this.externalTerm = "((\\s)?contract(\\s)?|(\\s)?tender documentation(\\s)?|(\\s)?technical specifications(\\s)?"
				+ "|(\\s)?contractor proposal(\\s)?|(\\s)?tendering documentation(\\s)?)";

		// Expressions

		this.numExp = "(([\\(]?((\\d)+|[a-zA-Z])[(\\.(\\d)+(\\.)?)]*[\\)]?)(?![a-zA-Z]).?|(\\(?(ix|iv|v?i{1,3}|x?i{1,3})\\)?)(?![a-zA-Z]).?)";
		this.valorExp = "((\\s|-|:|#|=)" + numExp + ")";
		this.ordinalExp = "(\\s)?(\\d)(st|nd|rd|th)(\\s)?";

		// Simple Grammar
		this.internalExp = "(" + markerTerm + valorExp + "|" + ordinalExp + markerTerm + ")";

		this.implicitComplexExp = implicitTerm + markerTerm + valorExp + "|" + implicitTerm + internalExp + "|"
				+ internalExp + implicitTerm;
		this.implicitExp = "(" + implicitComplexExp + "|" + implicitTerm + markerTerm + "|" + markerTerm + implicitTerm
				+ ")";
		this.simpleExp = "(" + internalExp + "|" + implicitExp + ")";

		// Complex Grammar
		this.multiValuedExp = "(" + internalExp + "(" + sepTerm + valorExp + ")*)";
		this.multiLayeredExp = "((" + internalExp + ")+" + multiValuedExp + "|" + multiValuedExp + "((" + sepTerm + "|"
				+ linkTerm + ")(" + internalExp + "|" + implicitExp + "))+|" + multiValuedExp + "(" + internalExp + "|"
				+ implicitExp + ")+|" + multiValuedExp + "((" + sepTerm + "|" + linkTerm + ")" + multiValuedExp + ")+|"
				+ internalExp + sepTerm + multiValuedExp + "|" + internalExp + "(" + linkTerm + multiValuedExp + ")+)";

		this.externalExp = "(" + markerTerm + valorExp + linkTerm + externalTerm + "|(" + multiValuedExp + "|"
				+ multiLayeredExp + ")" + linkTerm + externalTerm + "|(" + multiValuedExp + "|" + multiLayeredExp + ")"
				+ sepTerm + linkTerm + externalTerm + "|" + externalTerm + sepTerm + "described in" + "("
				+ multiLayeredExp + "|" + multiValuedExp + "))";

		this.complexExp = "(" + multiLayeredExp + "|" + multiValuedExp + ")";

		this.grammar = "(" + externalExp + "|" + complexExp + "|" + simpleExp + ")";

		this.bugPrefixs = new String[]{
		        "QBS",
				"QTBUG",
				"QT3DS" ,
				"AUTOSUITE" ,
				"QTJIRA" ,
				"QTCREATORBUG" ,
				"QDS" ,
				"PYSIDE" ,
				"QTIFW" ,
				"QTMOBILITY" ,
				"QTPLAYGROUND" ,
				"QTWEBSITE" ,
				"QTQAINFRA" ,
				"QTCOMPONENTS" ,
				"QSR" ,
				"QTSOLBUG" ,
				"QTVSADDINBUG" ,
				"QTWB" ,
				"QTSYSADM"};
	}

	/**
	 * Function to apply the grammar to each expression. The requirement
	 * identification is interrupted if more than 30 seconds pass. It returns the
	 * cross-reference dependencies.
	 * 
	 * @param expressionList
	 * @param expressionDB
	 * @return ArrayList
	 * @throws InterruptedException
	 */
	public ArrayList<Object> applyGrammar(ArrayList<Object> expressionList, ArrayList<Object> expressionDB)
			throws InterruptedException {

		ArrayList<Object> dependencies = new ArrayList<>();
		ArrayList<Object> dep = new ArrayList<>();

		Pattern pattern = Pattern.compile("(" + grammar + ")");
		for (Object expression : expressionList) {

			FutureTask task = new FutureTask(new CallableTask(expression, expressionDB, pattern, dep));
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(task);

			try {
				dep = (ArrayList<Object>) task.get(30, TimeUnit.SECONDS);

			} catch (Exception e) {
				System.err.println("[EXCEPTION] " + e + ": " + ((Bug) expression).toString());
			}

			executor.shutdown();

			for (Object d1 : dep) {
				dependencies.add(d1);
			}
		}
		// outputWriter.flush();
		// outputWriter.close();
		return dependencies;
	}

	/**
	 * Analyze the cross-referemce detected in the grammar for a Bug.
	 * 
	 * @param bug
	 * @param matcher
	 * @param bugList
	 * @return
	 */
	public ArrayList<Object> resolvingCrossReference(Bug bug, Matcher matcher,
			ArrayList<Object> bugList/*
										 * , BufferedWriter outputWriter
										 */) {
		ArrayList<Object> deps = new ArrayList<>();

		while (matcher.find()) {
			// System.out.println(bug.getId() + ": " + matcher.group(1));
			// outputWriter.write(bug.getId() + ": " + matcher.group(1));
			// outputWriter.newLine();
//			ArrayList<Integer> idIssues = new ArrayList<>();
			ArrayList<String> idBugs = new ArrayList<>();

			String aux = matcher.group(1).trim();
			for (String prefix : bugPrefixs) {
			    if (!prefix.toLowerCase().contains("qt"))
                    aux = aux.replaceAll("(?i)" + prefix + "(s(:?))?(-)?(\\s)?(#)?", prefix + "- ");
			    else {
			        String[] suffix = prefix.toLowerCase().split("qt");
			        aux = aux.replaceAll("(?i)(qt.*)?(-)?.*" + suffix[1] + "(s(:?))?(-)?(\\s)?(#)?", prefix + "- ");
                }

            }
			//.replaceAll("(qt.*)?(-)?.*bug(s(:?))?(-)?(\\s)?(#)?", "qtbug- ")

			aux = aux.replaceAll("[?]id=", " id= ");

			String[] terms = aux.split(" ");

			for (int i = 0; i < terms.length; i++) {
				String tmp = "";
				if (i < terms.length - 1) {
					tmp = terms[i + 1].replaceAll("((\\d)*)((.|;|:|,|\\(|\\))?)", "$1");
				}

				// Control the detected terms (qt case)
				switch (terms[i].toUpperCase()) {

                    case "QTBUG-":
                    case "QBS-":
                    case "AUTOSUITE-":
                    case "QTJIRA-":
                    case "QTCREATORBUG-":
                    case "QDS-":
                    case "PYSIDE-":
                    case "QTIFW-":
                    case "QTMOBILITY-":
                    case "QTPLAYGROUND-":
                    case "QTWEBSITE-":
                    case "QTQAINFRA-":
                    case "QTCOMPONENTS-":
                    case "QSR-":
                    case "QTSOLBUG-":
                    case "QTVSADDINBUG-":
                    case "QTWB-":
                    case "QTSYSADM-":
                        if (tmp.matches("\\d+")) {
                            idBugs.add(terms[i].toUpperCase() + tmp);
                        }
                        break;
//
                    case "and":
                    case "or":
                    case ",":
                    case "-":
                        if (tmp.matches("\\d+")) {
                            idBugs.add(terms[i].toUpperCase() + tmp);
                        }
                        break;
                    case "id=":
                        if (tmp.matches("\\d+")) {
                            idBugs.add(tmp);
                        }
                        break;
				}
			}
			// Create detected cross-reference
			for (int i = 0; i < bugList.size(); i++) {
				if ((bugList.get(i) instanceof Bug) && (
//						(idIssues.contains(((Bug) bugList.get(i)).getIssue())) ||
								(idBugs.contains(((Bug) bugList.get(i)).getId())))) {
					// Dependency dep = new Dependency(/*
					// * bug.getId(), ((Bug) bugList.get(i)).getId(),
					// */matcher.group(1), bug, bugList.get(i));

					Dependency dep = new Dependency(bug.getId(),
							((Bug) bugList.get(i)).getId(), Status.PROPOSED,
							DependencyType.CROSS_REFERENCE);
					deps.add(dep);
				}
			}
		}

		return deps;
	}

	/**
	 * Analyze the cross-reference detected in a grammar for a Clause.
	 * 
	 * @param clause
	 * @param matcher
	 * @param clauseList
	 * @return
	 */
	public ArrayList<Object> resolvingCrossReference(Clause clause, Matcher matcher,
			ArrayList<Object> clauseList/*
										 * , BufferedWriter outputWriter
										 */) {
		ArrayList<Object> deps = new ArrayList<>();

		while (matcher.find()) {
			HashMap<Integer, ArrayList<String>> location = new HashMap<>();
			// Arrays of document location
			location.put(0, new ArrayList<>()); // book
			location.put(1, new ArrayList<>()); // vol
			location.put(2, new ArrayList<>()); // part
			location.put(3, new ArrayList<>()); // sect
			location.put(4, new ArrayList<>()); // subsect
			location.put(5, new ArrayList<>()); // parag
			location.put(6, new ArrayList<>()); // subparag
			location.put(7, new ArrayList<>()); // unknown items (ambiguity)
			location.put(8, new ArrayList<>()); // External dependencies

			String textDetected = matcher.group(1).replaceAll("\\(|\\)", "");
			String[] terms = textDetected.split(" ");
			int currentLoc = -1;

			// Check if the detected term is an externat Expresion
			if (textDetected.matches(externalExp)) {
				currentLoc = 8;
				location.get(currentLoc).add(Integer.toString(clause.getId()));
				location.get(currentLoc).add(textDetected);
			}

			// controll detected terms in the grammar (file case)
			for (int i = 0; i < terms.length; i++) {
				switch (terms[i]) {
				case "article":
				case "articles":
					if ((i > 0) && (terms[i - 1].matches(implicitTerm))) {
						String[] removedNull = Arrays.stream(terms).filter(value -> value != null && value.length() > 0)
								.toArray(size -> new String[size]);
						if ((removedNull.length > 2)
								|| ((!terms[i - 1].equals("this")) && (!terms[i - 1].equals("same"))))
							if (!clause.getSubsect().isEmpty()) {
								currentLoc = 4;
								addElem(i, terms, location, currentLoc, clause.getSubsect(), clause, clauseList);
							} else {
								currentLoc = 3;
								addElem(i, terms, location, currentLoc, clause.getSect(), clause, clauseList);
							}

					} else {
						currentLoc = 8;
						if (location.get(currentLoc).isEmpty()) {
							location.get(currentLoc).add(Integer.toString(clause.getId()));
							location.get(currentLoc).add(textDetected);
						}
					}
					break;
				case "volume":
					currentLoc = 1;
					addElem(i, terms, location, currentLoc, clause.getVol(), clause, clauseList);
					break;
				case "lot":
				case "part":
					currentLoc = 2;
					addElem(i, terms, location, currentLoc, clause.getPart(), clause, clauseList);
					break;
				case "book":
				case "books":
					currentLoc = 0;
					addElem(i, terms, location, currentLoc, clause.getDoc(), clause, clauseList);

					break;
				case "paragraph":
				case "paragraphs":
					if ((i < terms.length - 1) && (terms[i + 1].matches("[a-zA-Z]|(ix|iv|v?i{1,3}|x?i{1,3})"))) {
						currentLoc = 6;
					} else {
						currentLoc = 5;
					}
					addElem(i, terms, location, currentLoc, clause.getParag(), clause, clauseList);
					break;
				case "subparagraph":
				case "subparagraphs":
				case "subitem":
				case "subitems":
					currentLoc = 6;
					addElem(i, terms, location, currentLoc, clause.getSubparg(), clause, clauseList);
					break;
				case "section":
				case "sect.":
				case "sections":
					currentLoc = 3;
					addElem(i, terms, location, currentLoc, clause.getSect(), clause, clauseList);
					break;
				case "item":
				case "items":
				case "point":
				case "points":
					if ((i < terms.length - 1) && (i > 0 && terms[i + 1].matches(numExp))
							&& (terms[i - 1].matches(implicitTerm))) {
						findAndFillLayers(6, terms[i + 1], clause, location, clauseList);

					} else if ((i < terms.length - 1) && (terms[i + 1].matches("(\\(?([a-zA-Z])\\)?)"))) {
						// Ambiguity
						currentLoc = 7;
						location.get(currentLoc).add(terms[i + 1].replaceAll("\\(|\\)", ""));
					} else if ((i < terms.length - 1) && (terms[i + 1].matches("(\\(?(\\d+|[a-zA-Z])\\)?)"))) {
						currentLoc = 3;
						location.get(currentLoc).add(terms[i + 1].replaceAll("\\(|\\)", ""));

					} else if ((i < terms.length - 1) && (terms[i + 1].matches(numExp))) {
						currentLoc = 4;
						terms[i + 1] = terms[i + 1].replaceAll("\\(|\\)", "");
						if (terms[i + 1].substring(terms[i + 1].length() - 1).equals(".")) {
							terms[i + 1] = terms[i + 1].substring(0, terms[i + 1].length() - 1);
						}
						location.get(currentLoc).add(terms[i + 1]);
					} else if ((i > 0) && (terms[i - 1].matches(implicitTerm))) {
						String[] removedNull = Arrays.stream(terms).filter(value -> value != null && value.length() > 0)
								.toArray(size -> new String[size]);
						if ((removedNull.length > 2)
								|| ((!terms[i - 1].equals("this")) && (!terms[i - 1].equals("same"))))
							if (!clause.getSubsect().isEmpty()) {
								currentLoc = 4;
								addElem(i, terms, location, currentLoc, clause.getSubsect(), clause, clauseList);
							} else if (!clause.getSect().isEmpty()) {
								currentLoc = 3;
								addElem(i, terms, location, currentLoc, clause.getSect(), clause, clauseList);
							}
					} else if ((i < terms.length - 1) && (terms[i + 1].matches(implicitTerm))) {
						if (!clause.getSubparg().isEmpty()) {
							currentLoc = 6;
							addElem(i, terms, location, currentLoc, clause.getSubparg(), clause, clauseList);
						} else if (!clause.getParag().isEmpty()) {
							currentLoc = 5;
							addElem(i, terms, location, currentLoc, clause.getParag(), clause, clauseList);
						} else if (!clause.getSubsect().isEmpty()) {
							currentLoc = 4;
							addElem(i, terms, location, currentLoc, clause.getSubsect(), clause, clauseList);
						} else if (!clause.getSect().isEmpty()) {
							currentLoc = 3;
							addElem(i, terms, location, currentLoc, clause.getSect(), clause, clauseList);
						}
					}
					break;
				case "subsection":
				case "subsect.":
				case "subsections":
					currentLoc = 4;
					addElem(i, terms, location, currentLoc, clause.getSubsect(), clause, clauseList);
					break;
				case "and":
				case "or":
				case ",":
				case "-":
					if (terms[i + 1].matches(numExp) || terms[i + 1].matches(ordinalExp)) {
						if (currentLoc != 8) {
							location.get(currentLoc).add(terms[i + 1].replaceAll("\\(|\\)", ""));
						}
					}
					break;
				case "to":
					String tmpLastTerm = terms[i - 1].replaceAll("\\(|\\)", "");
					String tmpNextTerm = "";
					if (terms[i + 1].matches(numExp)) {
						tmpNextTerm = terms[i + 1].replaceAll("\\(|\\)", "");
					} else if (terms[i + 1].matches(markerTerm)) {
						tmpNextTerm = terms[i + 2].replaceAll("\\(|\\)", "");
					}
					if (tmpNextTerm.matches("\\d+(\\.\\d(\\.)?)*")) {
						String tail = "", head = "", point = "";
						if (!tmpLastTerm.substring(tmpLastTerm.length() - 1).equals(".")) {
							tail = tmpLastTerm.substring(tmpLastTerm.length() - 1);
							head = tmpLastTerm.substring(0, tmpLastTerm.length() - 1);
						} else {
							tail = tmpLastTerm.substring(tmpLastTerm.length() - 2, tmpLastTerm.length() - 1);
							head = tmpLastTerm.substring(0, tmpLastTerm.length() - 2);
							point = ".";
						}
						int ini = Integer.parseInt(tail);

						tail = "";
						head = "";
						point = "";
						if (!tmpNextTerm.substring(tmpNextTerm.length() - 1).equals(".")) {
							tail = tmpNextTerm.substring(tmpNextTerm.length() - 1);
							head = tmpNextTerm.substring(0, tmpNextTerm.length() - 1);
						} else {
							tail = tmpNextTerm.substring(tmpNextTerm.length() - 2, tmpNextTerm.length() - 1);
							head = tmpNextTerm.substring(0, tmpNextTerm.length() - 2);
							point = ".";
						}
						int end = Integer.parseInt(tail);

						for (int j = ini; j <= end; j++) {
							location.get(currentLoc).add(head + Integer.toString(j) + point);
						}
					} else if (tmpNextTerm.matches("[a-zA-Z]")) {
						int ini = (int) tmpLastTerm.charAt(0);
						int end = (int) tmpNextTerm.charAt(0);
						for (int j = ini; (j <= end) && (j <= 122); j++) {
							location.get(currentLoc).add(Character.toString((char) j));
						}
					}
					break;

				}

			}

			// control ambiguous term locations
			if (!location.get(7).isEmpty()) {
				// if there is saved some ambiguous location
				int tmp = 2;
				for (int i = 3; i < 6; i++) {
					if (!location.get(i).isEmpty()) {
						tmp = i;
					}
				}
				for (String item : location.get(7)) {
					boolean find = false;
					if (tmp != 2) {
						for (int i = 6; (i > tmp) && (!find); i--) {
							for (int j = 0; (j < clauseList.size()) && (!find); j++) {
								if (clauseList.get(j) instanceof Clause) {
									switch (i) {
									case 3:
										if (item.equals(((Clause) clauseList.get(j)).getSect())) {
											location.get(i).add(item);
										}
										break;
									// case 4 is impossible to be a letter
									// because it is a sequence of subsections
									// (x.y.z.k)
									case 5:
										if ((item.equals(((Clause) clauseList.get(j)).getParag()))
												&& ((location.get(tmp).contains(((Clause) clauseList.get(j)).getSect()))
														|| (location.get(tmp).contains(
																((Clause) clauseList.get(j)).getSubsect())))) {
											location.get(i).add(item);
										}
										break;
									case 6:
										if ((item.equals(((Clause) clauseList.get(j)).getSubparg()))
												&& ((location.get(tmp).contains(((Clause) clauseList.get(j)).getSect()))
														|| (location.get(tmp)
																.contains(((Clause) clauseList.get(j)).getSubsect()))
														|| (location.get(tmp)
																.contains(((Clause) clauseList.get(j)).getParag())))) {
											location.get(i).add(item);
										}
										break;
									}
								}
							}
						}
					} else {
						findAndFillLayers(6, item, clause, location, clauseList);
					}
				}
			}

			// Check title, volume and part of the clause
			if (((currentLoc != -1) && (currentLoc != 8)) && (!location.get(currentLoc).isEmpty())
					&& ((location.get(0).isEmpty()) && (location.get(1).isEmpty()) && (location.get(2).isEmpty()))) {
				location.get(0).add(clause.getDoc());
				location.get(1).add(clause.getVol());
				location.get(2).add(clause.getPart());
			}

			// Detect dependencies
			for (int i = 0; i < clauseList.size(); i++) {
				boolean find = false;
				if ((clauseList.get(i) instanceof Clause)
						&& (location.get(0).contains(((Clause) clauseList.get(i)).getDoc())
								|| location.get(0).isEmpty())
						&& (location.get(1).contains(((Clause) clauseList.get(i)).getVol())
								|| location.get(1).isEmpty())
						&& (location.get(2).contains(((Clause) clauseList.get(i)).getPart())
								|| location.get(2).isEmpty())
						&& (location.get(3).contains(((Clause) clauseList.get(i)).getSect())
								|| location.get(3).isEmpty())

						&& (location.get(5).contains(((Clause) clauseList.get(i)).getParag())
								|| location.get(5).isEmpty())
						&& (location.get(6).contains(((Clause) clauseList.get(i)).getSubparg())
								|| location.get(6).isEmpty())
						&& (!location.get(0).isEmpty() || !location.get(1).isEmpty() || !location.get(2).isEmpty()
								|| !location.get(3).isEmpty() || !location.get(4).isEmpty()
								|| !location.get(5).isEmpty() || !location.get(6).isEmpty())) {
					if (!location.get(4).isEmpty())
						for (String subsect : location.get(4)) {
							try {
								if (subsect.equals(
										((Clause) clauseList.get(i)).getSubsect().substring(0, subsect.length()))) {
									find = true;
								}
							} catch (StringIndexOutOfBoundsException e) {

							}
						}
					else
						find = true;
					if (find) {
						// Dependency dep = new Dependency(/*
						// * clause.getId(), ((Clause) clauseList.get(i)).getId(),
						// */textDetected, clause, clauseList.get(i));
						Dependency dep = new Dependency(Integer.toString(clause.getId()),
								Integer.toString(((Clause) clauseList.get(i)).getId()), Status.PROPOSED,
								DependencyType.CROSS_REFERENCE);
						deps.add(dep);
					}
				}
			}
			
			// Save external dependencies
			if ((!location.get(0).isEmpty() || !location.get(1).isEmpty() || !location.get(2).isEmpty())
					&& (location.get(3).isEmpty() && location.get(4).isEmpty() && location.get(5).isEmpty()
							&& location.get(6).isEmpty())
					&& deps.isEmpty() && !location.get(8).contains(textDetected)) {
				location.get(8).add(Integer.toString(clause.getId()));
				location.get(8).add(textDetected);
			}

			for (int i = 0; i < location.get(8).size(); i = i + 2) {
				for (Object c : clauseList) {
					if (((Clause) c).getId() == Integer.parseInt(location.get(8).get(i))) {
						// ExternalDependency eDep = new ExternalDependency(location.get(8).get(i + 1),
						// c);
						
						Dependency extDep = new Dependency(Integer.toString(((Clause) c).getId()), "", Status.PROPOSED,
								DependencyType.EXTERNAL_CROSS_REFERENCE);
						deps.add(extDep);
					}
				}
			}
		}
		return deps;
	}

	/**
	 * Add an element to the location depending of the parameters. The function
	 * check if the clause location is a subsection and if it end with an endpoind
	 * or not.
	 * 
	 * @param i
	 * @param terms
	 * @param location
	 * @param currentLoc
	 * @param clauseLocation
	 * @param clause
	 * @param clauseList
	 */
	private void addElem(int i, String[] terms, HashMap<Integer, ArrayList<String>> location, int currentLoc,
			String clauseLocation, Clause clause, ArrayList<Object> clauseList) {

		String tail = "", head = "", point = "";

		if (clauseLocation != null)
			if (!clauseLocation.contains(".")) {
				tail = clauseLocation;
			} else if (!clauseLocation.substring(clauseLocation.length() - 1).equals(".")) {
				tail = clauseLocation.substring(clauseLocation.length() - 1);
				head = clauseLocation.substring(0, clauseLocation.length() - 1);
			} else {
				tail = clauseLocation.substring(clauseLocation.length() - 2, clauseLocation.length() - 1);
				head = clauseLocation.substring(0, clauseLocation.length() - 2);
				point = ".";
			}
		String next = "", prev = "";

		if ((i < terms.length - 1) && (i > 0) && (terms[i + 1].matches(numExp))
				&& (terms[i - 1].matches(implicitTerm))) {
			findAndFillLayers(currentLoc, terms[i + 1], clause, location, clauseList);
		} else if ((i < terms.length - 1) && (terms[i + 1].matches(numExp))) {
			location.get(currentLoc).add(terms[i + 1].replaceAll("\\(|\\)", ""));
		} else if ((i > 0) && (terms[i - 1].matches(ordinalExp))) {
			location.get(currentLoc).add(terms[i - 1].replaceAll("\\(|\\)|st|nd|rd|th", ""));
		} else if ((clauseLocation != null) && (i > 0) && (terms[i - 1].matches(implicitTerm))) {
			String[] removedNull = Arrays.stream(terms).filter(value -> value != null && value.length() > 0)
					.toArray(size -> new String[size]);
			// avoid this item
			if ((tail.length() > 2) && (currentLoc != 0)) {
				next = (head + Integer.toString((Integer.parseInt(tail) + 1)) + point);
				prev = (head + Integer.toString((Integer.parseInt(tail) - 1)) + point);
			} else {
				next = (head + Character.toString((char) ((int) tail.charAt(0) + 1)) + point);
				prev = (head + Character.toString((char) ((int) tail.charAt(0) - 1)) + point);
			}
			if ((removedNull.length > 2) || ((!terms[i - 1].equals("this")) && (!terms[i - 1].equals("same"))))
				addDirectImplicitElem(currentLoc, clause, clauseLocation, terms.length, terms[i - 1], location, next,
						prev);
		} else if ((clauseLocation != null) && (i < terms.length - 1) && (terms[i + 1].matches(implicitTerm))) {
			if ((tail.length() > 2) && (currentLoc != 0)) {
				next = (head + Integer.toString((Integer.parseInt(tail) + 1)) + point);
				prev = (head + Integer.toString((Integer.parseInt(tail) - 1)) + point);
			} else {
				next = (head + Character.toString((char) ((int) tail.charAt(0) + 1)) + point);
				prev = (head + Character.toString((char) ((int) tail.charAt(0) - 1)) + point);
			}
			addDirectImplicitElem(currentLoc, clause, clauseLocation, terms.length, terms[i + 1], location, next, prev);
		}

	}

	/**
	 * Add an implicit element knowing the location of the requirement by itself.
	 * 
	 * @param currentLoc
	 * @param clause
	 * @param sameElem
	 * @param lenghtListElem
	 * @param implicitTerm
	 * @param location
	 * @param nextElemToAdd
	 * @param prevElemToAdd
	 */
	private void addDirectImplicitElem(int currentLoc, Clause clause, String sameElem, int lenghtListElem,
			String implicitTerm, HashMap<Integer, ArrayList<String>> location, String nextElemToAdd,
			String prevElemToAdd) {
		switch (implicitTerm) {
		case "this":
		case "same":
		case "current":
			location.get(currentLoc).add(sameElem);
			fillWithMe(currentLoc, clause, location);
			break;
		case "preceding":
		case "previous":
			location.get(currentLoc).add(prevElemToAdd);
			fillWithMe(currentLoc, clause, location);
			break;
		case "next":
		case "following":
			location.get(currentLoc).add(nextElemToAdd);
			fillWithMe(currentLoc, clause, location);
			break;
		case "above":
			location.get(currentLoc).add(prevElemToAdd);
			fillWithMe(currentLoc, clause, location);
			break;
		case "below":
			location.get(currentLoc).add(nextElemToAdd);
			fillWithMe(currentLoc, clause, location);
			break;
		}

	}

	/**
	 * Fill the location of a requirement by the clause position.
	 * 
	 * @param currentLoc
	 * @param clause
	 * @param location
	 */
	private void fillWithMe(int currentLoc, Clause clause, HashMap<Integer, ArrayList<String>> location) {
		for (int i = currentLoc - 1; i > 2; i--) {
			switch (i) {
			case 5:
				location.get(i).add(clause.getParag());
				break;
			case 4:
				location.get(i).add(clause.getSubsect());
				break;
			case 3:
				location.get(i).add(clause.getSect());
				break;
			}

		}
	}

	/**
	 * Fill the location of a requirement by the clause position in the list.
	 * 
	 * @param currentLoc
	 * @param item
	 * @param clause
	 * @param location
	 * @param clauseList
	 */
	private void findAndFillLayers(int currentLoc, String item, Clause clause,
			HashMap<Integer, ArrayList<String>> location, ArrayList<Object> clauseList) {
		boolean find = false;
		for (int j = 0; (j < clauseList.size()) && (!find); j++) {
			if (clauseList.get(j) instanceof Clause) {
				for (int i = currentLoc; i > 2 && !find; i--) {
					switch (i) {
					case 3:
						if (item.equals(((Clause) clauseList.get(j)).getSect())) {
							location.get(i).add(item);
							find = true;
						}
						break;
					case 4:
						if (item.equals(((Clause) clauseList.get(j)).getSubsect())
								&& (clause.getSect().equals(((Clause) clauseList.get(j)).getSect()))) {
							location.get(i).add(item);
							location.get(3).add(clause.getSect());
							find = true;
						}
						break;
					case 5:
						if (item.equals(((Clause) clauseList.get(j)).getParag())
								&& (clause.getSubsect().equals(((Clause) clauseList.get(j)).getSubsect()))) {
							location.get(i).add(item);
							location.get(4).add(clause.getSubsect());
							find = true;
						}
						break;
					case 6:
						if (item.equals(((Clause) clauseList.get(j)).getSubparg())
								&& (clause.getSubsect().equals(((Clause) clauseList.get(j)).getSubsect()))) {
							location.get(i).add(item);
							location.get(4).add(clause.getSubsect());
							find = true;
						}
						break;
					}
				}
			}
		}
	}

}
