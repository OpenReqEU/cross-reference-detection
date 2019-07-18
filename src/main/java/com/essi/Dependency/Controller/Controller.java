package com.essi.Dependency.Controller;

import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import com.essi.Dependency.Components.Grammar;
import com.essi.Dependency.Service.GrammarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.essi.Dependency.Components.Dependency;
import com.essi.Dependency.Functionalities.JSONHandler;
import com.essi.Dependency.Service.DependencyService;
import com.essi.Dependency.Service.FileFormatException;
import com.essi.Dependency.Service.StorageFileNotFoundException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping("/upc/cross-reference-detection")
@Api(value = "ControllerAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class Controller {

    private static final String	    TABLE = "requirement";
    private static final String	    COLS  = "id.text.document.volume.part.sect.subsect.parag.subparag.";
    private final DependencyService depService;

//    @Autowired
//    private JdbcTemplate	    jdbcTemplate;

    /**
     * Constructor
     * 
     * @param depService
     *            - The API service
     */
    @Autowired
    public Controller(DependencyService depService) {
	this.depService = depService;
    }

    /*
     * Upload file detection
     */

    /**
     * Function to upload one document in HTML format to the server, extracts the
     * cross-references of all the requirements in the document and finally removes
     * the uploaded file.
     * 
     * @param file
     *            - The file to upload
     * @param redirectAttributes
     * @return 200 HTTPStatus code and Json list of dependencies
     * @throws IOException
     * @throws InterruptedException
     */
    @PostMapping("/file")
    // @RequestMapping(value = "/", headers = "content-type=multipart/*", method
    // =
    // RequestMethod.POST)
    @ApiOperation(value = "Uploads a file and detects its dependencies",
	    notes = "Uploads one document in HTML format to the server, extracts the cross-references of all the requirements in the document and finally removes the uploaded file.",
	    response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There is no content to submit."),
	    @ApiResponse(code = 200, message = "OK: The request has succeeded."),
	    @ApiResponse(code = 201,
		    message = "Created: The request has been fulfilled and has resulted in one or more new resources being created.",
		    response = String.class),
	    @ApiResponse(code = 401,
		    message = "Unauthorized: The request has not been applied because it lacks valid authentication credentials for the target resource."),
	    @ApiResponse(code = 403,
		    message = "Forbidden: The server understood the request but refuses to authorize it."),
	    @ApiResponse(code = 404,
		    message = "Not Found: The server could not find what was requested by the client."),
	    @ApiResponse(code = 500,
		    message = "Internal Server Error. For more information see ‘message’ in the Response Body.") })
    public ResponseEntity<?> crossReferenceDetector(
	    @ApiParam(value = "The file to upload (HTML format)",
		    required = true) @RequestParam("file") MultipartFile file,
		@ApiParam(value = "Company") @RequestParam(required = false) String company,
	    RedirectAttributes redirectAttributes) throws IOException, InterruptedException {

	long startTime = System.currentTimeMillis();
	long stopTime;
	long elapsedTime;
	ArrayList<Object> dependencies = new ArrayList<>();
	ObjectNode objN;
	ObjectNode node;
	// check the format of the file
	try {
	    if (!file.getOriginalFilename().contains("htm")) {
		throw new FileFormatException();
	    }

	    // initialize and store the folder to store the data file
	    depService.init();
	    depService.store(file);
	    redirectAttributes.addFlashAttribute("message",
		    "You successfully uploaded " + file.getOriginalFilename() + "!");

	    // extract clauses and locations from the input file (Clause.class)
	    ArrayList<Object> clauseList = depService.extractClauseList();
	    dependencies = depService.getDependencies(company);

	    // Create the new JSON to be returned (Project, requirements, dependencies).
	    JSONHandler jh = new JSONHandler();
	    objN = jh.storeDependencies("", dependencies);
	    node = jh.storeRequirements(objN, clauseList);
	    node = jh.createProject(node, clauseList);
	} catch (FileFormatException e) {
		// show the error with an entity format.
	    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "The format file must be htm or html.");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	stopTime = System.currentTimeMillis();
	elapsedTime = stopTime - startTime;
	// System.out.println("[TIME] /file: " + timeFormat(elapsedTime) +
	// "(mm:ss:mmss)");
	
	// Delete the input data file and folder.
	depService.deleteAll();
	return new ResponseEntity<>(node, HttpStatus.OK);
    }

    // /**
    // * Function to upload one document in HTML format to the server, extracts the
    // * cross-references of the first N requirements of the document and finally
    // * removes the uploaded file.
    // *
    // * @param file
    // * - The file to upload
    // * @param redirectAttributes
    // * @param n
    // * - Number of requirements that will be analyzed to extract
    // * dependencies
    // * @return 200 HTTPStatus code and Json list of dependencies
    // * @throws IOException
    // * @throws InterruptedException
    // */
    // @PostMapping("/file/{n}")
    // // @RequestMapping(value = "/", headers = "content-type=multipart/*", method
    // // =
    // // RequestMethod.POST)
    // @ApiOperation(value = "Uploads a file and detects the dependencies of first N
    // clauses",
    // notes = "Uploads one document in HTML format to the server, extracts the
    // cross-references of the first N requirements of the document and finally
    // removes the uploaded file.",
    // response = Dependency.class)
    // @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There
    // is no content to submit."),
    // @ApiResponse(code = 200, message = "OK: The request has succeeded."),
    // @ApiResponse(code = 201,
    // message = "Created: The request has been fulfilled and has resulted in one or
    // more new resources being created.",
    // response = String.class),
    // @ApiResponse(code = 401,
    // message = "Unauthorized: The request has not been applied because it lacks
    // valid authentication credentials for the target resource."),
    // @ApiResponse(code = 403,
    // message = "Forbidden: The server understood the request but refuses to
    // authorize it."),
    // @ApiResponse(code = 404,
    // message = "Not Found: The server could not find what was requested by the
    // client."),
    // @ApiResponse(code = 500,
    // message = "Internal Server Error. For more information see ‘message’ in the
    // Response Body.") })
    // public ResponseEntity<?> crossReferenceDetector(
    // @ApiParam(value = "The file to upload (HTML fromat)",
    // required = true) @RequestParam("file") MultipartFile file,
    // RedirectAttributes redirectAttributes,
    // @ApiParam(value = "Number of requirements that will be analyzed to extract
    // dependencies",
    // required = true) @PathVariable("n") String n)
    // throws IOException, InterruptedException {
    //
    // long startTime = System.currentTimeMillis();
    // long stopTime;
    // long elapsedTime;
    // ArrayList<Object> dependencies = new ArrayList<>();
    // try {
    // if (!file.getOriginalFilename().contains("htm")) {
    // throw new FileFormatException();
    // }
    // depService.store(file);
    // redirectAttributes.addFlashAttribute("message",
    // "You successfully uploaded " + file.getOriginalFilename() + "!");
    //
    // depService.extractClauseList();
    // dependencies = depService.getNDependencies(n);
    // depService.deleteAll();
    // depService.init();
    // } catch (FileFormatException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The format file must be htm or html.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (IndexOutOfBoundsException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' exceeds the index bounds.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (NumberFormatException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' must be an Integer.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (IllegalArgumentException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' must be bigger than 0 ( n > 0 ).");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // }
    //
    // stopTime = System.currentTimeMillis();
    // elapsedTime = stopTime - startTime;
    // // System.out.println("[TIME] /file/{n}: " + timeFormat(elapsedTime) +
    // // "(mm:ss:mmss)");
    // return new ResponseEntity<>(dependencies, HttpStatus.OK);
    // }

    /**
     * Function to upload one document in HTML format to the server, extracts the
     * cross-references of the requirements between N-M indexes in the document and
     * finally removes the uploaded file.
     * 
     * @param file
     *            - The file to upload
     * @param redirectAttributes
     * @param n
     *            - First index of the clause list that will be analysed (included)
     * @param m
     *            - Last index of the clause list that will be analysed (not
     *            included)
     * @return 200 HTTPStatus code and Json list of dependencies
     * @throws IOException
     * @throws InterruptedException
     */
    @PostMapping("/file/{n}/{m}")
    // @RequestMapping(value = "/", headers = "content-type=multipart/*", method
    // =
    // RequestMethod.POST)
    @ApiOperation(value = "Uploads a file and detects the dependencies between N-M clause's indexes",
	    notes = "Uploads one document in HTML format to the server, extracts the cross-references of the requirements between N-M indexes in the document and finally removes the uploaded file.",
	    response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There is no content to submit."),
	    @ApiResponse(code = 200, message = "OK: The request has succeeded."),
	    @ApiResponse(code = 201,
		    message = "Created: The request has been fulfilled and has resulted in one or more new resources being created.",
		    response = String.class),
	    @ApiResponse(code = 401,
		    message = "Unauthorized: The request has not been applied because it lacks valid authentication credentials for the target resource."),
	    @ApiResponse(code = 403,
		    message = "Forbidden: The server understood the request but refuses to authorize it."),
	    @ApiResponse(code = 404,
		    message = "Not Found: The server could not find what was requested by the client."),
	    @ApiResponse(code = 500,
		    message = "Internal Server Error. For more information see ‘message’ in the Response Body.") })
    public ResponseEntity<?> crossReferenceDetector(
	    @ApiParam(value = "The file to upload (HTML fromat)",
		    required = true) @RequestParam("file") MultipartFile file,
		@ApiParam(value = "Company") @RequestParam(required = false) String company,
	    RedirectAttributes redirectAttributes,
	    @ApiParam(value = "First index of the clause list that will be analysed (included)",
		    required = true) @PathVariable("n") String n,
	    @ApiParam(value = "Last index of the clause list that will be analysed (not included)",
		    required = true) @PathVariable("m") String m)
	    throws IOException, InterruptedException {

	long startTime = System.currentTimeMillis();
	long stopTime;
	long elapsedTime;
	ArrayList<Object> dependencies = new ArrayList<>();
	ObjectNode objN;
	
	// Check the correctness of the input data.
	try {
	    if (!file.getOriginalFilename().contains("htm")) {
		throw new FileFormatException();
	    }
	    if (Integer.parseInt(n) < 0 || Integer.parseInt(m) < Integer.parseInt(n)) {
		throw new IllegalArgumentException();
	    }

	    // Initialize and store the input file.
	    depService.init();
	    depService.store(file);
	    redirectAttributes.addFlashAttribute("message",
		    "You successfully uploaded " + file.getOriginalFilename() + "!");

	    // extract clauses and locations (Clause.class)
	    ArrayList<Object> clauseList = depService.extractClauseList();
	    dependencies = depService.getNMDependencies(company, n, m);
	    depService.deleteAll();

	    // Create the new Json (project, requirements, dependencies)
	    JSONHandler jh = new JSONHandler();
	    objN = jh.storeDependencies("", dependencies);
	    objN = jh.storeRequirements(objN, clauseList);
	    objN = jh.createProject(objN, clauseList);
	} catch (FileFormatException e) {
	    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "The format file must be htm or html.");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (IndexOutOfBoundsException e) {
	    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "Index exceeds the bounds.");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (NumberFormatException e) {
	    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "The parameters 'n' and 'm' must be Integers.");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (IllegalArgumentException e) {
	    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "The parameter 'n' must be bigger than 0 and lower than 'm' ( 0 < n < m ).");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	stopTime = System.currentTimeMillis();
	elapsedTime = stopTime - startTime;
	// System.out.println("[TIME] /file/{n}/{m}: " + timeFormat(elapsedTime)
	// +
	// "(mm:ss:mmss)");
	return new ResponseEntity<>(objN, HttpStatus.OK);
    }

    /**
     * Function to extract the cross-references of all bugs and requirements of a project stored in an input JSON.
     * 
     * @param json
     * @param redirectAttributes
     * @param projectId
     * @param request
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws InterruptedException
     * @throws FileFormatException
     */
    @PostMapping("/json/{projectId}")
    @ApiOperation(value = "Detects dependencies of the clauses stored in a JSON.",
	    notes = "Extracts the cross-references of all bugs and requirements of a project stored in an input JSON.",
	    response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There is no content to submit."),
	    @ApiResponse(code = 200, message = "OK: The request has succeeded."),
	    @ApiResponse(code = 401,
		    message = "Unauthorized: The request has not been applied because it lacks valid authentication credentials for the target resource."),
	    @ApiResponse(code = 403,
		    message = "Forbidden: The server understood the request but refuses to authorize it."),
	    @ApiResponse(code = 404,
		    message = "Not Found: The server could not find what was requested by the client."),
	    @ApiResponse(code = 500,
		    message = "Internal Server Error. For more information see ‘message’ in the Response Body.") })
    public ResponseEntity<?> crossReferenceDetectorJson(
	    @ApiParam(value = "The json object to upload.", required = true, example = "") @RequestBody String json,
		@ApiParam(value = "Company") @RequestParam(required = false) String company,
		RedirectAttributes redirectAttributes,
	    @ApiParam(value = "Id of the project where the requirements to analize are.",
		    required = true) @PathVariable("projectId") String projectId,
	    HttpServletRequest request)
	    throws IOException, ClassNotFoundException, SQLException, InterruptedException, FileFormatException {

	long startTime = System.currentTimeMillis();
	long stopTime;
	long elapsedTime;
	JSONHandler jh = new JSONHandler();

	// Create the JSONObject
	depService.storeJson(json);

	// Exrtact bug requirements from the JSON
	try {
	    ArrayList<ArrayList<String>> clauseList = jh.readRequirement(depService.getJson(), projectId);
	    depService.storeClauseList(clauseList);
	} catch (Exception e) {
		e.printStackTrace();
	    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	// Exrtact the dependencies from the bug requirements
	ArrayList<Object> dependencies = depService.getDependencies(company);
	
	// Save the detected cross-reference dependencies within the intput JSON
	ObjectNode objN = depService.storeDependenciesJson(dependencies);
	stopTime = System.currentTimeMillis();
	elapsedTime = stopTime - startTime;
	// System.out.println("[TIME] /database-Qt: " + timeFormat(elapsedTime)
	// +
	// "(mm:ss:mmss)");
//	depService.deleteAll();
	return new ResponseEntity<>(objN, HttpStatus.OK);
    }

    /**
     * Function to extract the cross-references between N-M indexes of all bugs and requirements of a project stored in an input JSON.
     * 
     * @param json
     * @param redirectAttributes
     * @param projectId
     * @param n
     * @param m
     * @param request
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws InterruptedException
     * @throws FileFormatException
     */
    @PostMapping("/json/{projectId}/{n}/{m}")
    @ApiOperation(value = "Detects dependencies between N-M indexes of the clauses stored in a JSON.",
	    notes = "Extracts the cross-references between N-M indexes of all bugs and requirements of a project stored in an input JSON.",
	    response = String.class)
    @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There is no content to submit."),
	    @ApiResponse(code = 200, message = "OK: The request has succeeded."),
	    @ApiResponse(code = 401,
		    message = "Unauthorized: The request has not been applied because it lacks valid authentication credentials for the target resource."),
	    @ApiResponse(code = 403,
		    message = "Forbidden: The server understood the request but refuses to authorize it."),
	    @ApiResponse(code = 404,
		    message = "Not Found: The server could not find what was requested by the client."),
	    @ApiResponse(code = 500,
		    message = "Internal Server Error. For more information see ‘message’ in the Response Body.") })
    public ResponseEntity<?> crossReferenceDetectorJson(
	    @ApiParam(value = "The json object to upload.", required = true) @RequestBody String json,
		@ApiParam(value = "Company") @RequestParam(required = false) String company,
		RedirectAttributes redirectAttributes,
	    @ApiParam(value = "Id of the project where the requirements to analize are.",
		    required = true) @PathVariable("projectId") String projectId,
	    @ApiParam(value = "First index of the requirement list that will be analysed (included)",
		    required = true) @PathVariable("n") String n,
	    @ApiParam(value = "Last index of the requirement list that will be analysed (not included)",
		    required = true) @PathVariable("m") String m,
	    HttpServletRequest request)
	    throws IOException, ClassNotFoundException, SQLException, InterruptedException, FileFormatException {

	long startTime = System.currentTimeMillis();
	long stopTime;
	long elapsedTime;
	JSONHandler jh = new JSONHandler();
	
	// Check the correctness of the input data
	if (Integer.parseInt(n) < 0 || Integer.parseInt(m) < Integer.parseInt(n)) {
	    throw new IllegalArgumentException();
	}

	// Create the JSONObject
	depService.storeJson(json);
	
	//Extract the bug requirements
	try {
	    ArrayList<ArrayList<String>> clauseList = jh.readRequirement(depService.getJson(), projectId);
	    depService.storeClauseList(clauseList);
	} catch (Exception e) {
	    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	// Get the dependencies from the bug requirements
	ArrayList<Object> dependencies = depService.getNMDependencies(company, n, m);

	// Save the dependencies into the input JSON
	ObjectNode objN = depService.storeDependenciesJson(dependencies);
	stopTime = System.currentTimeMillis();
	elapsedTime = stopTime - startTime;
	// System.out.println("[TIME] /database-Qt: " + timeFormat(elapsedTime)
	// +
	// "(mm:ss:mmss)");

//	depService.deleteAll();
	return new ResponseEntity<>(objN, HttpStatus.OK);
    }

    /* Old Data Base functionalities */
    //
    // /*
    // * Qt Data base detection
    // */
    //
    // /**
    // * Function to extract the cross-references of all bugs of a local database.
    // * This webservice is special for the QT trial, since some of the identified
    // * cross-references are a little bit different (as the webservice deals with
    // * bugs).
    // *
    // * @param dbName
    // * - Name of the data base
    // * @param tableName
    // * - Name of the table of the previous database in which the bugs are
    // * @param colsName
    // * - Names of the columns of the previous table in which the
    // * necessary information is, split by points and finished with an
    // * endpoint (e.g. 'col_1.col_2.'). Note: The sequence order must
    // * follow the structure of bug's identification (ID), bug's summary
    // * (SUMMARY), bug's description (DESCRIPTION) and bug's issue key
    // * identification (issuenum).
    // * @return List of dependencies
    // * @throws IOException
    // * @throws ClassNotFoundException
    // * @throws SQLException
    // * @throws InterruptedException
    // */
    // @RequestMapping(value = "/database-Qt/{dbName}/{tableName}/{colsName}",
    // method = RequestMethod.GET)
    // @ApiOperation(value = "Detects dependencies of bugs' Data Base.",
    // notes = "Extracts the cross-references of all bugs of a local database. This
    // webservice is special for the QT trial,"
    // + " since some of the identified cross-references are a little bit different
    // (as the webservice deals with bugs).",
    // response = Dependency.class)
    // @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There
    // is no content to submit."),
    // @ApiResponse(code = 200, message = "OK: The request has succeeded."),
    // @ApiResponse(code = 401,
    // message = "Unauthorized: The request has not been applied because it lacks
    // valid authentication credentials for the target resource."),
    // @ApiResponse(code = 403,
    // message = "Forbidden: The server understood the request but refuses to
    // authorize it."),
    // @ApiResponse(code = 404,
    // message = "Not Found: The server could not find what was requested by the
    // client."),
    // @ApiResponse(code = 500,
    // message = "Internal Server Error. For more information see ‘message’ in the
    // Response Body.") })
    // public ResponseEntity<?> crossReferenceDetectorQt(
    // @ApiParam(value = "Name of the data base", required = true)
    // @PathVariable("dbName") String dbName,
    // @ApiParam(value = "Name of the table of the previous database in which the
    // bugs are",
    // required = true) @PathVariable("tableName") String tableName,
    // @ApiParam(value = "Names of the columns of the previous table in which the
    // necessary information is,"
    // + " split by points and finished with an endpoint (e.g. 'col_1.col_2.')."
    // + " <br>Note: The sequence order must follow the structure of bug's
    // identification (ID),"
    // + " bug's summary (SUMMARY), bug's description (DESCRIPTION) and bug's issue
    // key identification (issuenum).",
    // required = true) @PathVariable("colsName") String colsName,
    // HttpServletRequest request) throws IOException, ClassNotFoundException,
    // SQLException, InterruptedException {
    //
    // long startTime = System.currentTimeMillis();
    // long stopTime;
    // long elapsedTime;
    //
    // try {
    // ArrayList<Object> clauseList = depService.extractClauseList(dbName, null,
    // tableName, colsName, jdbcTemplate,
    // request.getRemoteAddr());
    // } catch (Exception e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message",
    // "Could not get JDBC Connection; nested exception is java.sql.SQLException:
    // Could not connect.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    //
    // }
    // ArrayList<Object> dependencies = depService.getDependencies();
    //
    // stopTime = System.currentTimeMillis();
    // elapsedTime = stopTime - startTime;
    // // System.out.println("[TIME] /database-Qt: " + timeFormat(elapsedTime)
    // // +
    // // "(mm:ss:mmss)");
    // return new ResponseEntity<>(dependencies, HttpStatus.OK);
    // }
    //
    // /**
    // * Function to extract the cross-references of the first N bugs of a local
    // * database. This webservice is special for the QT trial, since some of the
    // * identified cross-references are a little bit different (as the webservice
    // * deals with bugs).
    // *
    // *
    // * @param dbName
    // * - Name of the data base
    // * @param tableName
    // * - Name of the table of the previous database in which the bugs are
    // * @param colsName
    // * - Names of the columns of the previous table in which the
    // * necessary information is, split by points and finished with an
    // * endpoint (e.g. 'col_1.col_2.'). Note: The sequence order must
    // * follow the structure of bug's identification (ID), bug's summary
    // * (SUMMARY), bug's description (DESCRIPTION) and bug's issue key
    // * identification (issuenum).
    // * @param n
    // * - Number of bugs that will be analysed to extract dependencies
    // * @return 200 HTTPStatus code and Json list of dependencies
    // * @throws IOException
    // * @throws ClassNotFoundException
    // * @throws SQLException
    // * @throws InterruptedException
    // */
    // @RequestMapping(value = "/database-Qt/{dbName}/{tableName}/{colsName}/{n}",
    // method = RequestMethod.GET)
    // @ApiOperation(value = "Detects dependencies of first N bugs in Data Base.",
    // notes = "Extracts the cross-references of the first N bugs of a local
    // database. This webservice is special for the QT trial,"
    // + " since some of the identified cross-references are a little bit different
    // (as the webservice deals with bugs).",
    // response = Dependency.class)
    // @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There
    // is no content to submit."),
    // @ApiResponse(code = 200, message = "OK: The request has succeeded."),
    // @ApiResponse(code = 401,
    // message = "Unauthorized: The request has not been applied because it lacks
    // valid authentication credentials for the target resource."),
    // @ApiResponse(code = 403,
    // message = "Forbidden: The server understood the request but refuses to
    // authorize it."),
    // @ApiResponse(code = 404,
    // message = "Not Found: The server could not find what was requested by the
    // client."),
    // @ApiResponse(code = 500,
    // message = "Internal Server Error. For more information see ‘message’ in the
    // Response Body.") })
    // public ResponseEntity<?> crossReferenceDetectorQt(
    // @ApiParam(value = "Name of the data base", required = true)
    // @PathVariable("dbName") String dbName,
    // @ApiParam(value = "Name of the table of the previous database in which the
    // bugs are",
    // required = true) @PathVariable("tableName") String tableName,
    // @ApiParam(value = "Names of the columns of the previous table in which the
    // necessary information is,"
    // + " split by points and finished with an endpoint (e.g. 'col_1.col_2.')."
    // + " <br>Note: The sequence order must follow the structure of bug's
    // identification (ID),"
    // + " bug's summary (SUMMARY), bug's description (DESCRIPTION) and bug's issue
    // key identification (issuenum).",
    // required = true) @PathVariable("colsName") String colsName,
    // @ApiParam(value = "Number of bugs that will be analysed to extract
    // dependencies",
    // required = true) @PathVariable("n") String n,
    // HttpServletRequest request) throws IOException, ClassNotFoundException,
    // SQLException, InterruptedException {
    //
    // long startTime = System.currentTimeMillis();
    // long stopTime;
    // long elapsedTime;
    // ArrayList<Object> dependencies = new ArrayList<>();
    //
    // try {
    // depService.extractClauseList(dbName, null, tableName, colsName, jdbcTemplate,
    // request.getRemoteAddr());
    // dependencies = depService.getNDependencies(n);
    // } catch (IndexOutOfBoundsException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' exceeds the index bounds.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (NumberFormatException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' must be an Integer.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (IllegalArgumentException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' must be bigger than 0 ( n > 0 ).");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (SQLException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message",
    // "Could not get JDBC Connection; nested exception is java.sql.SQLException:
    // Could not connect.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    //
    // }
    //
    // stopTime = System.currentTimeMillis();
    // elapsedTime = stopTime - startTime;
    // // System.out.println("[TIME] /database-Qt/{n}: " +
    // // timeFormat(elapsedTime) +
    // // "(mm:ss:mmss)");
    // return new ResponseEntity<>(dependencies, HttpStatus.OK);
    // }
    //
    // /**
    // * Function to extract the cross-references of the bugs between N-M indexes of
    // a
    // * local database. This webservice is special for the QT trial, since some of
    // * the identified cross-references are a little bit different (as the
    // webservice
    // * deals with bugs).
    // *
    // * @param dbName
    // * - Name of the data base
    // * @param tableName
    // * - Name of the table of the previous database in which the bugs are
    // * @param colsName
    // * - Names of the columns of the previous table in which the
    // * necessary information is, split by points and finished with an
    // * endpoint (e.g. 'col_1.col_2.'). Note: The sequence order must
    // * follow the structure of bug's identification (ID), bug's summary
    // * (SUMMARY), bug's description (DESCRIPTION) and bug's issue key
    // * identification (issuenum).
    // * @param n
    // * - First index of the bug list that will be analysed (included)
    // * @param m
    // * - Last index of the bug list that will be analysed (not included)
    // * @return 200 HTTPStatus code and Json list of dependencies
    // * @throws IOException
    // * @throws ClassNotFoundException
    // * @throws SQLException
    // * @throws InterruptedException
    // */
    // @RequestMapping(value =
    // "/database-Qt/{dbName}/{tableName}/{colsName}/{n}/{m}", method =
    // RequestMethod.GET)
    // @ApiOperation(value = "Detects dependencies between N-M indexes of bugs' Data
    // Base.",
    // notes = "Extracts the cross-references of the bugs between N-M indexes of a
    // local database. This webservice is special for the QT trial,"
    // + " since some of the identified cross-references are a little bit different
    // (as the webservice deals with bugs).",
    // response = Dependency.class)
    // @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There
    // is no content to submit."),
    // @ApiResponse(code = 200, message = "OK: The request has succeeded."),
    // @ApiResponse(code = 401,
    // message = "Unauthorized: The request has not been applied because it lacks
    // valid authentication credentials for the target resource."),
    // @ApiResponse(code = 403,
    // message = "Forbidden: The server understood the request but refuses to
    // authorize it."),
    // @ApiResponse(code = 404,
    // message = "Not Found: The server could not find what was requested by the
    // client."),
    // @ApiResponse(code = 500,
    // message = "Internal Server Error. For more information see ‘message’ in the
    // Response Body.") })
    // public ResponseEntity<?> crossReferenceDetectorQt(
    // @ApiParam(value = "Name of the data base", required = true)
    // @PathVariable("dbName") String dbName,
    // @ApiParam(value = "Name of the table of the previous database in which the
    // bugs are",
    // required = true) @PathVariable("tableName") String tableName,
    // @ApiParam(value = "Names of the columns of the previous table in which the
    // necessary information is,"
    // + " split by points and finished with an endpoint (e.g. 'col_1.col_2.')."
    // + " <br>Note: The sequence order must follow the structure of bug's
    // identification (ID),"
    // + " bug's summary (SUMMARY), bug's description (DESCRIPTION) and bug's issue
    // key identification (issuenum).",
    // required = true) @PathVariable("colsName") String colsName,
    // @ApiParam(value = "First index of the bug list that will be analysed
    // (included)",
    // required = true) @PathVariable("n") String n,
    // @ApiParam(value = "Last index of the bug list that will be analysed (not
    // included)",
    // required = true) @PathVariable("m") String m,
    // HttpServletRequest request) throws IOException, ClassNotFoundException,
    // SQLException, InterruptedException {
    //
    // long startTime = System.currentTimeMillis();
    // long stopTime;
    // long elapsedTime;
    // ArrayList<Object> dependencies = new ArrayList<>();
    //
    // try {
    // if (Integer.parseInt(n) < 0 || Integer.parseInt(m) < Integer.parseInt(n)) {
    // throw new IllegalArgumentException();
    // }
    // depService.extractClauseList(dbName, null, tableName, colsName, jdbcTemplate,
    // request.getRemoteAddr());
    // dependencies = depService.getNMDependencies(n, m);
    // } catch (IndexOutOfBoundsException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "Index exceeds the bounds.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (NumberFormatException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameters 'n' and 'm' must be Integers.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (IllegalArgumentException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' must be bigger than 0 and lower than
    // 'm' ( 0 < n < m ).");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (SQLException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message",
    // "Could not get JDBC Connection; nested exception is java.sql.SQLException:
    // Could not connect.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    //
    // }
    //
    // stopTime = System.currentTimeMillis();
    // elapsedTime = stopTime - startTime;
    // // System.out.println("[TIME] /database-Qt/{n}/{m}: " +
    // // timeFormat(elapsedTime)
    // // + "(mm:ss:mmss)");
    // return new ResponseEntity<>(dependencies, HttpStatus.OK);
    // }
    //
    // /*
    // * OpenReq Data Base detection
    // */
    //
    // /**
    // * Funtion to extract the cross-references of all the requirements of a local
    // * database. The database needs to follow the schema of the OpenReq database.
    // *
    // * @param dbName
    // * - Name of the data base
    // * @return List of dependencies
    // * @throws IOException
    // * @throws ClassNotFoundException
    // * @throws SQLException
    // * @throws InterruptedException
    // */
    // @RequestMapping(value = "/database/{dbName}/{projectName}", method =
    // RequestMethod.GET)
    // @ApiOperation(value = "Detects dependencies of clauses' Data Base.",
    // notes = "Extracts the cross-references of all the requirements of a local
    // database. The database needs to follow the schema of the OpenReq database.",
    // response = Dependency.class)
    // @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There
    // is no content to submit."),
    // @ApiResponse(code = 200, message = "OK: The request has succeeded."),
    // @ApiResponse(code = 401,
    // message = "Unauthorized: The request has not been applied because it lacks
    // valid authentication credentials for the target resource."),
    // @ApiResponse(code = 403,
    // message = "Forbidden: The server understood the request but refuses to
    // authorize it."),
    // @ApiResponse(code = 404,
    // message = "Not Found: The server could not find what was requested by the
    // client."),
    // @ApiResponse(code = 500,
    // message = "Internal Server Error. For more information see ‘message’ in the
    // Response Body.") })
    // public ResponseEntity<?> crossReferenceDetectorS(
    // @ApiParam(value = "Name of the data base", required = true)
    // @PathVariable("dbName") String dbName,
    // @ApiParam(value = "Name of the project", required = true)
    // @PathVariable("projectName") String projectName,
    // HttpServletRequest request) throws IOException, ClassNotFoundException,
    // SQLException, InterruptedException {
    //
    // long startTime = System.currentTimeMillis();
    // long stopTime;
    // long elapsedTime;
    //
    // try {
    // ArrayList<Object> clauseList = depService.extractClauseList(dbName,
    // projectName, TABLE, COLS, jdbcTemplate,
    // request.getRemoteAddr());
    // } catch (SQLException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message",
    // "Could not get JDBC Connection; nested exception is java.sql.SQLException:
    // Could not connect.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    //
    // }
    // ArrayList<Object> dependencies = depService.getDependencies();
    //
    // stopTime = System.currentTimeMillis();
    // elapsedTime = stopTime - startTime;
    // // System.out.println("[TIME] /database: " + timeFormat(elapsedTime) +
    // // "(mm:ss:mmss)");
    // return new ResponseEntity<>(dependencies, HttpStatus.OK);
    // }
    //
    // /**
    // * Function to extract the cross-references of the first N requirements of a
    // * local database. The database needs to follow the schema of the OpenReq
    // * database.
    // *
    // * @param dbName
    // * - Name of the data base
    // * @param n
    // * - Number of bugs that will be analysed to extract dependencies
    // * @return 200 HTTPStatus code and Json list of dependencies
    // * @throws IOException
    // * @throws ClassNotFoundException
    // * @throws SQLException
    // * @throws InterruptedException
    // */
    // @RequestMapping(value = "/database/{dbName}/{projectName}/{n}", method =
    // RequestMethod.GET)
    // @ApiOperation(value = "Detects dependencies of first N clauses in Data
    // Base.",
    // notes = "Extracts the cross-references of the first N requirements of a local
    // database. The database needs to follow the schema of the OpenReq database.",
    // response = Dependency.class)
    // @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There
    // is no content to submit."),
    // @ApiResponse(code = 200, message = "OK: The request has succeeded."),
    // @ApiResponse(code = 401,
    // message = "Unauthorized: The request has not been applied because it lacks
    // valid authentication credentials for the target resource."),
    // @ApiResponse(code = 403,
    // message = "Forbidden: The server understood the request but refuses to
    // authorize it."),
    // @ApiResponse(code = 404,
    // message = "Not Found: The server could not find what was requested by the
    // client."),
    // @ApiResponse(code = 500,
    // message = "Internal Server Error. For more information see ‘message’ in the
    // Response Body.") })
    // public ResponseEntity<?> crossReferenceDetectorS(
    // @ApiParam(value = "Name of the data base", required = true)
    // @PathVariable("dbName") String dbName,
    // @ApiParam(value = "Name of the project", required = true)
    // @PathVariable("projectName") String projectName,
    // @ApiParam(value = "Number of clauses that will be analysed to extract
    // dependencies",
    // required = true) @PathVariable("n") String n,
    // HttpServletRequest request) throws IOException, ClassNotFoundException,
    // SQLException, InterruptedException {
    //
    // long startTime = System.currentTimeMillis();
    // long stopTime;
    // long elapsedTime;
    // ArrayList<Object> dependencies = new ArrayList<>();
    //
    // try {
    // depService.extractClauseList(dbName, projectName, TABLE, COLS, jdbcTemplate,
    // request.getRemoteAddr());
    // dependencies = depService.getNDependencies(n);
    // } catch (IndexOutOfBoundsException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' exceeds the index bounds.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (NumberFormatException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' must be an Integer.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (IllegalArgumentException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' must be bigger than 0 ( n > 0 ).");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (SQLException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message",
    // "Could not get JDBC Connection; nested exception is java.sql.SQLException:
    // Could not connect.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    //
    // }
    //
    // stopTime = System.currentTimeMillis();
    // elapsedTime = stopTime - startTime;
    // // System.out.println("[TIME] /database/{n}: " + timeFormat(elapsedTime)
    // // +
    // // "(mm:ss:mmss)");
    // return new ResponseEntity<>(dependencies, HttpStatus.OK);
    // }
    //
    // /**
    // * Function to extract the cross-references of the requirements between N-M
    // * indexes of a local database. The database needs to follow the schema of the
    // * OpenReq database.
    // *
    // * @param dbName
    // * - Name of the data base
    // * @param n
    // * - First index of the bug list that will be analysed (included)
    // * @param m
    // * - Last index of the bug list that will be analysed (not included)
    // * @return 200 HTTPStatus code and Json list of dependencies
    // * @throws IOException
    // * @throws ClassNotFoundException
    // * @throws SQLException
    // * @throws InterruptedException
    // */
    // @RequestMapping(value = "/database/{dbName}/{projectName}/{n}/{m}", method =
    // RequestMethod.GET)
    // @ApiOperation(value = "Detects dependencies between N-M indexes of clauses'
    // Data Base.",
    // notes = "Extracts the cross-references of the requirements between N-M
    // indexes of a local database."
    // + " The database needs to follow the schema of the OpenReq database.",
    // response = Dependency.class)
    //
    // @ApiResponses(value = { @ApiResponse(code = 0, message = "Non content: There
    // is no content to submit."),
    // @ApiResponse(code = 200, message = "OK: The request has succeeded."),
    // @ApiResponse(code = 401,
    // message = "Unauthorized: The request has not been applied because it lacks
    // valid authentication credentials for the target resource."),
    // @ApiResponse(code = 403,
    // message = "Forbidden: The server understood the request but refuses to
    // authorize it."),
    // @ApiResponse(code = 404,
    // message = "Not Found: The server could not find what was requested by the
    // client."),
    // @ApiResponse(code = 500,
    // message = "Internal Server Error. For more information see ‘message’ in the
    // Response Body.") })
    // public ResponseEntity<?> crossReferenceDetectorS(
    // @ApiParam(value = "Name of the data base", required = true)
    // @PathVariable("dbName") String dbName,
    // @ApiParam(value = "Name of the project", required = true)
    // @PathVariable("projectName") String projectName,
    // @ApiParam(value = "First index of the clause list that will be analysed
    // (included)",
    // required = true) @PathVariable("n") String n,
    // @ApiParam(value = "Last index of the clause list that will be analysed (not
    // included)",
    // required = true) @PathVariable("m") String m,
    // HttpServletRequest request) throws IOException, ClassNotFoundException,
    // SQLException, InterruptedException {
    //
    // long startTime = System.currentTimeMillis();
    // long stopTime;
    // long elapsedTime;
    // ArrayList<Object> dependencies = new ArrayList<>();
    //
    // try {
    // if (Integer.parseInt(n) < 0 || Integer.parseInt(m) < Integer.parseInt(n)) {
    // throw new IllegalArgumentException();
    // }
    // depService.extractClauseList(dbName, projectName, TABLE, COLS, jdbcTemplate,
    // request.getRemoteAddr());
    // dependencies = depService.getNMDependencies(n, m);
    // } catch (IndexOutOfBoundsException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "Index exceeds the bounds.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (NumberFormatException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameters 'n' and 'm' must be Integers.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (IllegalArgumentException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message", "The parameter 'n' must be bigger than 0 and lower than
    // 'm' ( 0 < n < m ).");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (SQLException e) {
    // LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // result.put("status", "500");
    // result.put("error", "Internal Server Error");
    // result.put("exception", e.toString());
    // result.put("message",
    // "Could not get JDBC Connection; nested exception is java.sql.SQLException:
    // Could not connect.");
    // return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    //
    // }
    //
    // stopTime = System.currentTimeMillis();
    // elapsedTime = stopTime - startTime;
    // // System.out.println("[TIME] /database/{n}/{m}: " +
    // // timeFormat(elapsedTime) +
    // // "(mm:ss:mmss)");
    // return new ResponseEntity<>(dependencies, HttpStatus.OK);
    // }

    /*
     * Old Functionalities
     */

    //
    // @PostMapping("/upload")
    // // @RequestMapping(value = "/", headers = "content-type=multipart/*",
    // method
    // =
    // // RequestMethod.POST)
    // @ApiOperation(value = "Uploads a file.", notes = "Upload one document to
    // the
    // API to be analized.", response = String.class, responseContainer = "OK:
    // The
    // request has succeeded.")
    // @ApiResponses(value = { @ApiResponse(code = 200, message = "OK: The
    // request
    // has succeeded."),
    // @ApiResponse(code = 201, message = "Created: The request has been
    // fulfilled
    // and has resulted in one or more new resources being created."),
    // @ApiResponse(code = 401, message = "Unauthorized: The request has not
    // been
    // applied because it lacks valid authentication credentials for the target
    // resource."),
    // @ApiResponse(code = 403, message = "Forbidden: The server understood the
    // request but refuses to authorize it."),
    // @ApiResponse(code = 404, message = "Not Found: The server could not find
    // what
    // was requested by the client.") })
    // public String handleFileUpload(
    // @ApiParam(value = "The file to upload", required = true)
    // @RequestParam("file") MultipartFile file,
    // RedirectAttributes redirectAttributes) {
    //
    // long startTime = System.currentTimeMillis();
    // depService.store(file);
    // redirectAttributes.addFlashAttribute("message",
    // "You successfully uploaded " + file.getOriginalFilename() + "!");
    //
    // long stopTime = System.currentTimeMillis();
    // long elapsedTime = stopTime - startTime;
    // System.out.println("[TIME] Upload file: " + timeFormat(elapsedTime) +
    // "(mm:ss:mmss)");
    // return "redirect:/";
    // }
    //
    // @RequestMapping(value = "/extractClauses", method = RequestMethod.GET)
    // @ApiOperation(value = "Extract clauses of an html document.", notes =
    // "Extract all clauses of a previously upload html document.", response =
    // String.class, responseContainer = "OK: The request has succeeded.")
    // @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response
    // =
    // String.class),
    // @ApiResponse(code = 401, message = "Unauthorized: The request has not
    // been
    // applied because it lacks valid authentication credentials for the target
    // resource."),
    // @ApiResponse(code = 403, message = "Forbidden: The server understood the
    // request but refuses to authorize it."),
    // @ApiResponse(code = 404, message = "Not Found: The server could not find
    // what
    // was requested by the client.") })
    // public ArrayList<Object> extractClauseList() throws IOException {
    // long startTime = System.currentTimeMillis();
    // ArrayList<Object> clauseList = depService.extractClauseList();
    // long stopTime = System.currentTimeMillis();
    // long elapsedTime = stopTime - startTime;
    // System.out.println("[TIME] Extract from file: " + timeFormat(elapsedTime)
    // +
    // "(mm:ss:mmss)");
    // return clauseList;
    // }
    //
    // // colsName should be split by "." and an end point.
    // @RequestMapping(value =
    // "/extractClauses/{dbName}/{tableName}/{colsName}",
    // method = RequestMethod.GET)
    // @ApiOperation(value = "Extract bugs of an DataBase.", notes = "Extract
    // all
    // bugs of a local DataBase.", response = String.class, responseContainer =
    // "OK:
    // The request has succeeded.")
    // @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response
    // =
    // String.class),
    // @ApiResponse(code = 401, message = "Unauthorized: The request has not
    // been
    // applied because it lacks valid authentication credentials for the target
    // resource."),
    // @ApiResponse(code = 403, message = "Forbidden: The server understood the
    // request but refuses to authorize it."),
    // @ApiResponse(code = 404, message = "Not Found: The server could not find
    // what
    // was requested by the client.") })
    // public ArrayList<Object> extractClauseList(
    // @ApiParam(value = "Name of the data base", required = true)
    // @PathVariable("dbName") String dbName,
    // @ApiParam(value = "Table's name of previous data base", required = true)
    // @PathVariable("tableName") String tableName,
    // @ApiParam(value = "Name of the columns of previous table split by a point
    // (e.g. 'col_1.col_2')", required = true) @PathVariable("colsName") String
    // colsName)
    // throws IOException, ClassNotFoundException, SQLException {
    //
    // long startTime = System.currentTimeMillis();
    // ArrayList<Object> clauseList = depService.extractClauseList(dbName,
    // tableName, colsName);
    // long stopTime = System.currentTimeMillis();
    // long elapsedTime = stopTime - startTime;
    // System.out.println("[TIME] Extract from DB: " + timeFormat(elapsedTime) +
    // "(mm:ss:mmss)");
    // return clauseList;
    // }
    //
    // @RequestMapping(value = "/grammar", method = RequestMethod.GET)
    // @ApiOperation(value = "Identify dependencies.", notes = "Apply grammar to
    // identify requirements and analyze them to extract dependencies.",
    // response =
    // String.class, responseContainer = "OK: The request has succeeded.")
    // @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response
    // =
    // String.class),
    // @ApiResponse(code = 401, message = "Unauthorized: The request has not
    // been
    // applied because it lacks valid authentication credentials for the target
    // resource."),
    // @ApiResponse(code = 403, message = "Forbidden: The server understood the
    // request but refuses to authorize it."),
    // @ApiResponse(code = 404, message = "Not Found: The server could not find
    // what
    // was requested by the client.") })
    // public ArrayList<Object> getDependencies() throws IOException,
    // InterruptedException {
    // long startTime = System.currentTimeMillis();
    // ArrayList<Object> dependencies = depService.getDependencies();
    // long stopTime = System.currentTimeMillis();
    // long elapsedTime = stopTime - startTime;
    // System.out.println("[TIME] Apply grammar (all items): " +
    // timeFormat(elapsedTime) + "(mm:ss:mmss)\n");
    // return dependencies;
    // }
    //
    // @RequestMapping(value = "/grammar/{n}", method = RequestMethod.GET)
    // @ApiOperation(value = "Identify N dependencies.", notes = "Apply grammar
    // to
    // identify requirements and analyze the first N ones to extract
    // dependencies.",
    // response = String.class, responseContainer = "OK: The request has
    // succeeded.")
    // @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response
    // =
    // String.class),
    // @ApiResponse(code = 401, message = "Unauthorized: The request has not
    // been
    // applied because it lacks valid authentication credentials for the target
    // resource."),
    // @ApiResponse(code = 403, message = "Forbidden: The server understood the
    // request but refuses to authorize it."),
    // @ApiResponse(code = 404, message = "Not Found: The server could not find
    // what
    // was requested by the client.") })
    // public ArrayList<Object> getDependencies(
    // @ApiParam(value = "The first n dependencies", required = true)
    // @PathVariable("n") String n)
    // throws IOException, InterruptedException {
    // long startTime = System.currentTimeMillis();
    // ArrayList<Object> dependencies = depService.getNDependencies(n);
    // long stopTime = System.currentTimeMillis();
    // long elapsedTime = stopTime - startTime;
    // System.out.println("[TIME] Apply grammar (" + n + " items): " +
    // timeFormat(elapsedTime) + "(mm:ss:mmss)\n");
    // return dependencies;
    // }
    //
    // @RequestMapping(value = "/grammar/{n}/{m}", method = RequestMethod.GET)
    // @ApiOperation(value = "Identify dependencies between N-M requirements.",
    // notes = "Apply grammar to identify requirements and analyze them between
    // N-M
    // indexes to extract dependencies.", response = String.class,
    // responseContainer
    // = "OK: The request has succeeded.")
    // @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response
    // =
    // String.class),
    // @ApiResponse(code = 401, message = "Unauthorized: The request has not
    // been
    // applied because it lacks valid authentication credentials for the target
    // resource."),
    // @ApiResponse(code = 403, message = "Forbidden: The server understood the
    // request but refuses to authorize it."),
    // @ApiResponse(code = 404, message = "Not Found: The server could not find
    // what
    // was requested by the client.") })
    // public ArrayList<Object> getDependencies(
    // @ApiParam(value = "Index n of list requirements (included)", required =
    // true)
    // @PathVariable("n") String n,
    // @ApiParam(value = "Index m of list requirements (not included)", required
    // =
    // true) @PathVariable("m") String m)
    // throws IOException, InterruptedException {
    // long startTime = System.currentTimeMillis();
    // ArrayList<Object> dependencies = depService.getNMDependencies(n, m);
    // long stopTime = System.currentTimeMillis();
    // long elapsedTime = stopTime - startTime;
    // System.out.println("[TIME] Apply grammar (" + (Integer.parseInt(m) -
    // Integer.parseInt(n)) + " items): "
    // + timeFormat(elapsedTime) + "(mm:ss:mmss)\n");
    // return dependencies;
    // }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
	return ResponseEntity.notFound().build();
    }

    /**
     * Function to extract the format of the time in case it is calculated. (its functions are commented in the code)
     * @param millis
     * @return
     */
    private String timeFormat(long millis) {
	String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
		TimeUnit.MILLISECONDS.toSeconds(millis)
			- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
		millis - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)));

	return time;
    }

}
