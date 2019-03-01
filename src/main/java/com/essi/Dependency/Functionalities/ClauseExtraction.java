package com.essi.Dependency.Functionalities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import org.apache.tomcat.jdbc.pool.DataSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.essi.Dependency.Components.Clause;

import com.essi.Dependency.Components.Bug;

import java.sql.*;

@Repository
public class ClauseExtraction {
    private static int		     currentPart    = 0;
    private static int		     currentSection = 0;
    private static int		     currentParag   = 0;
    private static int		     clauseNumber   = 0;

    private static String	     currentSubsect = "0";
    private static String	     currentVolume  = null;
    private static String	     currentDoc	    = null;
    private static ArrayList<Object> clauseList	    = new ArrayList<>();

    private static String	     part	    = Integer.toString(currentPart);
    private static String	     sect	    = null;
    private static String	     parag	    = null;
    private static String	     subparg	    = null;
    private static String	     filename;

    private static String	     nextSubsection = "0";
    private static String	     newSubsection  = null;

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
	ClauseExtraction.filename = filename;
    }

    /**
     * getters
     * 
     * @return String
     */
    public static String getFilename() {
	return filename;
    }

    /**
     * Extract the requirements of a database.
     *
     * @param dbName
     * @param tableName
     * @param colsName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    // public ArrayList<Object> databaseExtraction(String dbName, String tableName,
    // String colsName, String projectName)
    // throws ClassNotFoundException, SQLException {
    //
    // Statement stmt = null;
    // String myDriver = "org.gjt.mm.mysql.Driver";
    // String myUrl = "jdbc:mysql://localhost/" + dbName;
    // Class.forName(myDriver);
    // Connection conn = DriverManager.getConnection(myUrl, "root", "");
    //
    // String[] columns = colsName.split("\\.");
    // String strCol = "";
    // for (String c : columns) {
    // strCol = strCol + tableName + "." + c + ", ";
    // }
    // strCol = strCol.substring(0, strCol.length() - 2);
    // String query;
    // if (projectName == null) {
    // query = "SELECT " + strCol + " FROM `" + tableName + "`";
    // } else {
    // query = "SELECT " + strCol + " FROM `" + tableName + "` INNER JOIN `projects`
    // ON " + tableName
    // + ".idProject=projects.id WHERE projects.name = '" + projectName + "'";
    // }
    // // generate list of bugs/clauses
    // ArrayList<Object> objctList = new ArrayList<>();
    // try {
    // stmt = conn.createStatement();
    // ResultSet rs = stmt.executeQuery(query);
    // while (rs.next()) {
    // ArrayList<String> components = new ArrayList<>();
    // for (String c : columns) {
    // components.add(rs.getString(c));
    // }
    // if (components.size() > 4) {
    // Clause clause = new Clause(components);
    // objctList.add(clause);
    // } else {
    // Bug bug = new Bug(components);
    // objctList.add(bug);
    // }
    // }
    // } catch (SQLException e) {
    // System.out.println("[ERROR] " + e);
    // } finally {
    // if (stmt != null) {
    // stmt.close();
    // }
    // }
    //
    // return objctList;
    // }

    /**
     * Extract the requirements of a database using JDBC.
     * 
     * @param dbName
     * @param tableName
     * @param colsName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
//    public ArrayList<Object> databaseExtractionJDBC(String dbName, String tableName, String colsName,
//	    String projectName, JdbcTemplate jdbcTemplate, String remoteAddr)
//	    throws ClassNotFoundException, SQLException {
//
//	if (remoteAddr.equals("0:0:0:0:0:0:0:1")) {
//	    remoteAddr = "localhost";
//	}
//	
//	jdbcTemplate = new JdbcTemplate((DataSource) DataSourceBuilder.create().username("root").password("")
//		.url("jdbc:mariadb://" + remoteAddr + "/" + dbName).driverClassName("org.mariadb.jdbc.Driver").build());
//
//	String[] columns = colsName.split("\\.");
//	String strCol = "";
//	for (String c : columns) {
//	    strCol = strCol + tableName + "." + c + ", ";
//	}
//	strCol = strCol.substring(0, strCol.length() - 2);
//
//	// generate list of bugs/clauses
//	ArrayList<Object> objctList = new ArrayList<>();
//
//	String query;
//	if (projectName == null) {
//	    List<Bug> lc = new ArrayList<>();
//	    query = "SELECT " + strCol + " FROM `" + tableName + "`";
//	    lc = jdbcTemplate.query(query, (rs, rowNum) -> new Bug(Integer.parseInt(rs.getString(columns[0])),
//		    Integer.parseInt(rs.getString(columns[3])), rs.getString(columns[1]), rs.getString(columns[2])));
//	    for (Bug c : lc) {
//		objctList.add(c);
//	    }
//	    return objctList;
//	} else {
//	    List<Clause> lc = new ArrayList<>();
//	    query = "SELECT " + strCol + " FROM `" + tableName + "` INNER JOIN `projects` ON " + tableName
//		    + ".idProject=projects.id WHERE projects.name = '" + projectName + "'";
//	    lc = jdbcTemplate.query(query,
//		    (rs, rowNum) -> new Clause(rs.getString(columns[2]), rs.getString(columns[3]),
//			    rs.getString(columns[4]), rs.getString(columns[5]), rs.getString(columns[6]),
//			    rs.getString(columns[7]), rs.getString(columns[8]), rs.getString(columns[1]),
//			    Integer.parseInt(rs.getString(columns[0]))));
//	    for (Clause c : lc) {
//		objctList.add(c);
//	    }
//	}
//	return objctList;
//    }

//    private DataSource newDataSource() {
//	return (DataSource) DataSourceBuilder.create().username("").password("").url("").driverClassName("").build();
//    }

    /**
     * Extract the HTML clauses into an Array.
     * 
     * @param path
     * @return
     * @throws IOException
     */
    public ArrayList<Object> HTMLToArray(String path) throws IOException {
	reinizializeAtr();
	String p = path.replaceAll("\\|\\\\", "/");
	File input = new File(p);

	// Parse the selected document using specified charset.
	Document doc = Jsoup.parse(input, "windows-1252");

	// Get all body elements with <p> or <h*> tag.
	Elements elements = doc.body().select("p, h, h1, h2, h3, h4, h5, h6");
	// ArrayList<clause> clauses = new ArrayList<clause>();
	String name[] = input.getName().split("\\.");
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
     * @throws IOException
     */
    private static void extractComposition(String line) throws IOException {

	int nextSection = currentSection + 1;
	// for (String line : lines) {

	if (line.matches("^([vV][oO][lL][uU][mM][eE][\\s]?(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})[\\d]?).*")) {
	    currentVolume = line.replaceAll("\\s|[vV][oO][lL][uU][mM][eE]", "");
	    return;
	}
	// detect part or lot (only digits, not roman numbers!).
	if (line.matches("^([pP][aA][rR][tT][\\s]?[\\d]?).*") | line.matches("^([lL][oO][tT][\\s]?[\\d]?).*")) {
	    String[] parts = line.split(" ");
	    part = parts[1].replaceAll("\\D+", "");
	    return;
	}

	if (line.matches("^" + nextSection + "([\\.])?([\\s])+([\\w+\\s])+[^\\.{3,}].*")) {
	    currentSection = nextSection;
	    nextSubsection = Integer.toString(currentSection) + ".0";
	    nextSection++;
	    sect = Integer.toString(currentSection);
	    if (!clauseList.isEmpty()) {
		extractClauses(clauseList, line, currentDoc, currentVolume, part, sect, currentSubsect, parag, subparg);
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
		extractClauses(clauseList, line, currentDoc, currentVolume, part, sect, currentSubsect, parag, subparg);
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
		extractClauses(clauseList, line, currentDoc, currentVolume, part, sect, currentSubsect, parag, subparg);
	    }
	    currentParag = 0;
	    return;

	}
	int i = 3, p = 2;
	boolean find = false;
	String prevSubsection;
	String[] nexts = nextSubsection.split("\\.");
	while (!find && i < nextSubsection.length()) {

	    String head = "", tail = "";
	    for (int j = 0; j < nexts.length; j++) {
		if (j < nexts.length - p)
		    head = head + nexts[j] + ".";
		else if (j < nexts.length - (p - 1))
		    tail = tail + (Integer.parseInt(nexts[j]) + 1);
	    }
	    prevSubsection = head + tail;

	    if (line.matches("^" + (prevSubsection) + "([\\.])?([\\s])+([\\w+\\s])+.*")) {
		currentSubsect = prevSubsection;
		newSubsection = prevSubsection + ".1";
		nextSubsection = prevSubsection;

		if (!clauseList.isEmpty()) {
		    extractClauses(clauseList, line, currentDoc, currentVolume, part, sect, currentSubsect, parag,
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
		subparg = numeration[0];
	    } else if (line.matches("\\(+((xl|l?x{0,3})(ix|iv|v?i{0,3}))+\\).*")) {
		numeration = line.split(" ");
		subparg = numeration[0].replaceAll("\\)|\\(", "");
		subparg.replaceAll("\\(", "");
	    }
	    extractClauses(clauseList, line, currentDoc, currentVolume, part, sect, currentSubsect, parag, subparg);
	    subparg = null;
	    return;
	}

	return;
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
     * @param parag
     * @param subparg
     */
    private static void extractClauses(ArrayList<Object> clauseList, String line, String doc, String vol, String part,
	    String sect, String subsect, String parag, String subparg) {
	currentParag++;
	parag = Integer.toString(currentParag);
	String[] cls = line.split("\\.([\\s]+(?=[A-Z\\n])|(?=[a-z]+\\)))");
	for (String c : cls) {
	    if (!c.equals(" ")) {
		if (c.matches("^[(xl|l?x{0,3})(ix|iv|v?i{0,3})].*") | line.matches("^[a-zA-Z].*")
			| line.matches("^[\\d].*")) {
		    c = c.replaceFirst("%", ".");
		}
		clauseList.add(new Clause(doc, vol, part, sect, subsect, parag, subparg, c, clauseNumber));
		clauseNumber++;
	    }
	}
    }

    /**
     * Write the expressions (clauses, bugs) into an external document.
     * 
     * @param filename
     * @param printID
     * @throws IOException
     */
    public void writeClauses(String filename, Boolean printID) throws IOException {
	BufferedWriter outputWriter = null;
	outputWriter = new BufferedWriter(new FileWriter(filename));
	for (Object c : clauseList) {
	    if (printID) {
		outputWriter.write(((Clause) c).getId() + "- ");
	    }
	    outputWriter.write(((Clause) c).printMe());
	    outputWriter.write("\n");
	    outputWriter.newLine();
	}
	outputWriter.flush();
	outputWriter.close();
    }

    /**
     * Write all list into an external document.
     * 
     * @param filename
     * @param list
     * @throws IOException
     */
    public <T> void writeList(String filename, ArrayList<T> list) throws IOException {
	BufferedWriter outputWriter = null;
	outputWriter = new BufferedWriter(new FileWriter(filename));
	for (T object : list) {
	    outputWriter.write(object.toString());
	    outputWriter.newLine();
	}
	outputWriter.flush();
	outputWriter.close();
    }
}
