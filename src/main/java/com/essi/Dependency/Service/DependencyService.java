package com.essi.Dependency.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.essi.Dependency.Repository.GrammarRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.essi.Dependency.Components.Bug;
import com.essi.Dependency.Components.Clause;
import com.essi.Dependency.Functionalities.ClauseExtraction;
import com.essi.Dependency.Functionalities.Grammar;
import com.essi.Dependency.Functionalities.JSONHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class DependencyService {

	@Autowired
	private GrammarRepository grammarRepository;

	private final Path rootLocation;
	private ClauseExtraction clauseExtraction;
	private Grammar grammar;

	private ArrayList<Object> clauseList;
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
	public void storeClauseList(ArrayList<ArrayList<String>> clauseList) {
		this.clauseList = new ArrayList<>();
		for (ArrayList<String> reqArray : clauseList) {
			if (reqArray.size() == 3) {
				try {
					Bug b = new Bug(reqArray.get(0),
							reqArray.get(1), reqArray.get(2));
					this.clauseList.add(b);
				} catch (java.lang.NumberFormatException e) {
					System.out.println(e.getLocalizedMessage());
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
	public ArrayList<Object> extractClauseList() throws IOException {
		String path = load(this.filename).toString();
		this.clauseList = clauseExtraction.HTMLToArray(path);
		return clauseList;
	}

	/**
	 * Service function to call the identification method of a cross-reference in a
	 * data base.
	 * 
	 * @param dbName
	 * @param tableName
	 * @param colsName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	// public ArrayList<Object> extractClauseList(String dbName, String tableName,
	// String colsName)
	// throws IOException, ClassNotFoundException, SQLException {
	// this.clauseList = clauseExtraction.databaseExtraction(dbName, tableName,
	// colsName, null);
	// return clauseList;
	// }

	/**
	 * Service function to call the identification method of a cross-reference in a
	 * data base.
	 * 
	 * @param dbName
	 * @param tableName
	 * @param colsName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	// public ArrayList<Object> extractClauseList(String dbName, String projectName,
	// String tableName, String colsName)
	// throws IOException, ClassNotFoundException, SQLException {
	// this.clauseList = clauseExtraction.databaseExtraction(dbName, tableName,
	// colsName, projectName);
	// return clauseList;
	// }

	/**
	 * Service function to call the identification method of a cross-reference in a
	 * data base by JDBC.
	 * 
	 * @param dbName
	 * @param tableName
	 * @param colsName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
//    public ArrayList<Object> extractClauseList(String dbName, String projectName, String tableName, String colsName,
//	    JdbcTemplate jdbcTemplate, String remoteAddr) throws IOException, ClassNotFoundException, SQLException {
//	this.clauseList = clauseExtraction.databaseExtractionJDBC(dbName, tableName, colsName, projectName,
//		jdbcTemplate, remoteAddr);
//	return clauseList;
//    }

	/**
	 * Service function to get the dependencies after applying the grammar and
	 * analyze the expressions.
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public ArrayList<Object> getDependencies(String company) throws IOException, InterruptedException {
		com.essi.Dependency.Components.Grammar grammarObj = grammarRepository.findByCompany(company);
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
	public ArrayList<Object> getNMDependencies(String company, String n, String m) throws IOException, InterruptedException {
		com.essi.Dependency.Components.Grammar grammarObj = grammarRepository.findByCompany(company);
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
	public ObjectNode storeDependenciesJson(ArrayList<Object> dependencies) throws IOException {
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
