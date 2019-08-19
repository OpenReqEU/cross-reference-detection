package com.essi.dependency.controller;

import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.essi.dependency.functionalities.JSONHandler;
import com.essi.dependency.util.Control;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.essi.dependency.service.DependencyService;
import com.essi.dependency.service.FileFormatException;
import com.essi.dependency.service.StorageFileNotFoundException;
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

    private final DependencyService depService;

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
    public ResponseEntity crossReferenceDetector(
	    @ApiParam(value = "The file to upload (HTML format)",
		    required = true) @RequestParam("file") MultipartFile file,
		@ApiParam(value = "Company") @RequestParam(required = false) String company,
	    RedirectAttributes redirectAttributes) throws IOException, InterruptedException {

	List<Object> dependencies;
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
		List<Object> clauseList = depService.extractClauseList();
	    dependencies = depService.getDependencies(company);

	    // Create the new JSON to be returned (Project, requirements, dependencies).
	    JSONHandler jh = new JSONHandler();
	    objN = jh.storeDependencies("", dependencies);
	    node = jh.storeRequirements(objN, clauseList);
	    node = jh.createProject(node, clauseList);
	} catch (FileFormatException e) {
		// show the error with an entity format.
	    LinkedHashMap<String, String> result = new LinkedHashMap<>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "The format file must be htm or html.");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	// Delete the input data file and folder.
	depService.deleteAll();
	return new ResponseEntity<>(node, HttpStatus.OK);
    }

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
    public ResponseEntity crossReferenceDetector(
	    @ApiParam(value = "The file to upload (HTML fromat)",
		    required = true) @RequestParam("file") MultipartFile file,
		@ApiParam(value = "Company") @RequestParam(required = false) String company,
	    RedirectAttributes redirectAttributes,
	    @ApiParam(value = "First index of the clause list that will be analysed (included)",
		    required = true) @PathVariable("n") String n,
	    @ApiParam(value = "Last index of the clause list that will be analysed (not included)",
		    required = true) @PathVariable("m") String m)
	    throws IOException, InterruptedException {

	List<Object> dependencies = new ArrayList<>();
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
	    List<Object> clauseList = depService.extractClauseList();
	    dependencies = depService.getNMDependencies(company, n, m);
	    depService.deleteAll();

	    // Create the new Json (project, requirements, dependencies)
	    JSONHandler jh = new JSONHandler();
	    objN = jh.storeDependencies("", dependencies);
	    objN = jh.storeRequirements(objN, clauseList);
	    objN = jh.createProject(objN, clauseList);
	} catch (FileFormatException e) {
	    LinkedHashMap<String, String> result = new LinkedHashMap<>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "The format file must be htm or html.");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (IndexOutOfBoundsException e) {
	    LinkedHashMap<String, String> result = new LinkedHashMap<>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "Index exceeds the bounds.");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (NumberFormatException e) {
	    LinkedHashMap<String, String> result = new LinkedHashMap<>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "The parameters 'n' and 'm' must be Integers.");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	} catch (IllegalArgumentException e) {
	    LinkedHashMap<String, String> result = new LinkedHashMap<>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "The parameter 'n' must be bigger than 0 and lower than 'm' ( 0 < n < m ).");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}
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
    public ResponseEntity crossReferenceDetectorJson(
	    @ApiParam(value = "The json object to upload.", required = true, example = "") @RequestBody String json,
		@ApiParam(value = "Company") @RequestParam(required = false) String company,
		RedirectAttributes redirectAttributes,
	    @ApiParam(value = "Id of the project where the requirements to analize are.",
		    required = true) @PathVariable("projectId") String projectId,
	    HttpServletRequest request)
	    throws IOException, ClassNotFoundException, SQLException, InterruptedException, FileFormatException {

	JSONHandler jh = new JSONHandler();

	// Create the JSONObject
	depService.storeJson(json);

	// Exrtact bug requirements from the JSON
	try {
	    List<List<String>> clauseList = jh.readRequirement(depService.getJson(), projectId);
	    depService.storeClauseList(clauseList);
	} catch (Exception e) {
		Control.getInstance().showErrorMessage(e.getMessage());
	    LinkedHashMap<String, String> result = new LinkedHashMap<>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	// Exrtact the dependencies from the bug requirements
	List<Object> dependencies = depService.getDependencies(company);
	
	// Save the detected cross-reference dependencies within the intput JSON
	ObjectNode objN = depService.storeDependenciesJson(dependencies);
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
    public ResponseEntity crossReferenceDetectorJson(
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

	JSONHandler jh = new JSONHandler();
	
	// Check the correctness of the input data
	if (Integer.parseInt(n) < 0 || Integer.parseInt(m) < Integer.parseInt(n)) {
	    throw new IllegalArgumentException();
	}

	// Create the JSONObject
	depService.storeJson(json);
	
	//Extract the bug requirements
	try {
	    List<List<String>> clauseList = jh.readRequirement(depService.getJson(), projectId);
	    depService.storeClauseList(clauseList);
	} catch (Exception e) {
	    LinkedHashMap<String, String> result = new LinkedHashMap<>();
	    result.put("status", "500");
	    result.put("error", "Internal Server Error");
	    result.put("exception", e.toString());
	    result.put("message", "");
	    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	// Get the dependencies from the bug requirements
	List<Object> dependencies = depService.getNMDependencies(company, n, m);

	// Save the dependencies into the input JSON
	ObjectNode objN = depService.storeDependenciesJson(dependencies);
	return new ResponseEntity<>(objN, HttpStatus.OK);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
	return ResponseEntity.notFound().build();
    }

}
