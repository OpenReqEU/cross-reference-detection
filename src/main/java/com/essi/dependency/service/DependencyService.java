package com.essi.dependency.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.essi.dependency.repository.GrammarRepository;
import com.essi.dependency.util.Control;
import com.essi.dependency.components.Bug;
import com.essi.dependency.functionalities.ClauseExtraction;
import com.essi.dependency.functionalities.Grammar;
import com.essi.dependency.functionalities.JSONHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class DependencyService {

	@Autowired
	private GrammarRepository grammarRepository;

	private final Path rootLocation;
	private ClauseExtraction clauseExtraction;
	private Grammar grammar;

	private List<Object> clauseList;
	private String filename;
	private String json;

	/**
	 * Constructor
	 * 
	 * @param properties
	 */
	@Autowired
	public DependencyService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
		this.clauseExtraction = new ClauseExtraction();
		this.grammar = new Grammar(null);
	}

	/**
	 * Getters and setters
	 */
	public String getJson() {
		return json;
	}

	public String getFilename() {
		return this.filename;
	}

	public String getFilenamePath() {
		return load(this.filename).toString();
	}

	/**
	 * Store requirements into the list
	 * @param clauseList
	 */
	public void storeClauseList(List<List<String>> clauseList) {
		this.clauseList = new ArrayList<>();
		for (List<String> reqArray : clauseList) {
			if (reqArray.size() == 3) {
				try {
					Bug b = new Bug(reqArray.get(0),
							reqArray.get(1), reqArray.get(2));
					this.clauseList.add(b);
				} catch (java.lang.NumberFormatException e) {
					Control.getInstance().showErrorMessage(e.getLocalizedMessage());
				}
			} else if (reqArray.size() > 3) {
				try {
					Bug b = new Bug(reqArray.get(0),
							reqArray.get(1), reqArray.get(2), reqArray.subList(3, reqArray.size()));
					this.clauseList.add(b);
				} catch (java.lang.NumberFormatException e) {
					Control.getInstance().showErrorMessage(e.getLocalizedMessage());
				}
			}
		}
	}

	/**
	 * Function to store a file into rootLocation.
	 * 
	 * @param file
	 */
	public void store(MultipartFile file) {
		this.filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + this.filename);
			}
			if (this.filename.contains("..")) {
				// This is a security check
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + this.filename);
			}
			Files.copy(file.getInputStream(), this.rootLocation.resolve(this.filename),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + this.filename, e);
		}
		clauseExtraction.setFilename(this.filename);
	}

	/**
	 * Function to store JSON String.
	 * 
	 * @param json
	 */
	public void storeJson(String json) {
		this.json = json;
		this.filename = "json-file";
		clauseExtraction.setFilename(this.filename);
	}

	/**
	 * Function to load the stored file.
	 * 
	 * @param filename
	 * @return
	 */
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	/**
	 * Service function to call the extraction of the cross-reference if the
	 * clause's list.
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<Object> extractClauseList() throws IOException {
		String path = load(this.filename).toString();
		this.clauseList = clauseExtraction.HtmlToArray(path);
		return clauseList;
	}

	/**
	 * Service function to get the dependencies after applying the grammar and
	 * analyze the expressions.
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public List<Object> getDependencies(String company) throws InterruptedException {
		com.essi.dependency.components.Grammar grammarObj = grammarRepository.findByCompany(company);
		return this.grammar.applyGrammar(grammarObj, this.clauseList, this.clauseList);
	}

	/**
	 * Service function to get the dependencies between N-M indexes of the
	 * clauseList after applying the grammar and analyze the expressions.
	 * 
	 * @param n
	 * @param m
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public List<Object> getNMDependencies(String company, String n, String m) throws InterruptedException {
		com.essi.dependency.components.Grammar grammarObj = grammarRepository.findByCompany(company);
		return this.grammar.applyGrammar(
				grammarObj,
				new ArrayList<Object>(this.clauseList.subList(Integer.parseInt(n), Integer.parseInt(m))),
				this.clauseList);
	}

	/**
	 * Function to store the detected dependencies into the input JSON
	 * 
	 * @param dependencies
	 * @return
	 * @throws IOException
	 */
	public ObjectNode storeDependenciesJson(List<Object> dependencies) throws IOException {
		return new JSONHandler().storeDependencies(json, dependencies);
	}

	/**
	 * Function to delete the rootLocation path and all its files.
	 */
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	/**
	 * Function to create the rootLocation path.
	 */
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

}
