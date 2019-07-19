package com.essi.Dependency.Functionalities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.essi.Dependency.Components.Bug;
import com.essi.Dependency.Components.Clause;
import com.essi.Dependency.Components.Dependency;

public class CallableTask implements Callable<ArrayList<Object>> {

	private final Object expression;
	private final ArrayList<Object> expressionList;
	private final Pattern pattern;
	private ArrayList<Object> dep;
	private Grammar grammar;

	/**
	 * Constructor
	 * 
	 * @param expression
	 * @param expressionList
	 * @param pattern
	 * @param dep
	 */
	CallableTask(com.essi.Dependency.Components.Grammar grammarObj, Object expression, ArrayList<Object> expressionList, Pattern pattern, ArrayList<Object> dep) {
		this.expression = expression;
		this.expressionList = expressionList;
		this.pattern = pattern;
		this.dep = dep;
		grammar = new Grammar(grammarObj);
	}

	/**
	 * Run method of the Callable Thread. Identify if it is a bug or a clause and
	 * apply the detection of the cross-reference.
	 */

	@Override
	public ArrayList<Object> call() {
		if (expression instanceof Clause) {

			((Clause) expression).setClauseString(((Clause) expression).getClauseString().toLowerCase().replaceAll(",", " ,"));
			Matcher matcher = pattern.matcher(((Clause) expression).getClauseString());

			dep = grammar.resolvingCrossReference(((Clause) expression), matcher, expressionList/* , outputWriter */);

		} else if (expression instanceof Bug) {

			// Apply the grammar to the summary
			Matcher matcher;
			if (((Bug) expression).getSummary() != null) {

				matcher = pattern.matcher(((Bug) expression).getSummary().toLowerCase().replaceAll(",", " ,")
						.replaceAll("[?]id=", " ?id="));
				dep = grammar.resolvingCrossReference(((Bug) expression), matcher, expressionList/* , outputWriter */);
			}
			// Apply the grammar to the description
			ArrayList<Object> tmp = new ArrayList<>();
			if (((Bug) expression).getDescription() != null) {
				matcher = pattern.matcher(((Bug) expression).getDescription().toLowerCase().replaceAll(",", " ,")
						.replaceAll("[?]id=", " ?id="));
				tmp = grammar.resolvingCrossReference(((Bug) expression), matcher, expressionList/*
																									 * , outputWriter
																									 */);
			}
			List<String> comments = ((Bug) expression).getComments();
			if (comments != null && !comments.isEmpty()) {
				for (String comment : comments) {
					matcher = pattern.matcher(comment.toLowerCase().replaceAll(",", " ,")
							.replaceAll("[?]id=", " ?id="));
					tmp.addAll(grammar.resolvingCrossReference(((Bug) expression), matcher, expressionList));
				}
			}
			// check if the dependency from summary bug or description bug is repeated.
			boolean find;
			for (Object t : tmp) {
				find = false;
				for (Object d : dep) {
					if ((((Dependency) t).getFrom().equals(((Dependency) d).getFrom())
							&& ((Dependency) t).getTo().equals(((Dependency) d).getTo()))
							|| ((Dependency) t).getFrom().equals(((Dependency) d).getTo())
									&& ((Dependency) t).getTo().equals(((Dependency) d).getFrom()))
						find = true;
				}
				if (!find)
					dep.add(t);
			}
		}
		return dep;
	}

}
