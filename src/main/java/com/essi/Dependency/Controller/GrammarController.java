package com.essi.Dependency.Controller;

import com.essi.Dependency.Components.Grammar;
import com.essi.Dependency.Service.GrammarService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/upc/cross-reference-detection")
@Api(value = "GrammarControllerAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class GrammarController {

    @Autowired
    private GrammarService grammarService;

    @PostMapping("/grammar")
    @ApiOperation(value = "Store grammar",
            notes = "Given a company, stores its list of prefixes to use as grammar rules to extract requirement cross-references",
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
    public ResponseEntity<?> uploadGrammar(
            @ApiParam(value = "The grammar",
                    required = true) @RequestBody Grammar grammar,
            @ApiParam(value = "Company", required = true) @RequestParam("company") String company) {
        try {
            grammarService.uploadGrammar(company, grammar);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new HttpEntity<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/grammar")
    @ApiOperation(value = "Update grammar",
            notes = "Given a company, updates its existing list of prefixes to use as grammar rules to extract requirement cross-references",
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
    public ResponseEntity<?> updateGrammar(
            @ApiParam(value = "The grammar",
                    required = true) @RequestBody Grammar grammar,
            @ApiParam(value = "Company", required = true) @RequestParam("company") String company) {
        try {
            grammarService.updateGrammar(company, grammar);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new HttpEntity<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/grammar")
    @ApiOperation(value = "Delete grammar",
            notes = "Deletes grammar rules of a given company",
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
    public ResponseEntity<?> deleteGrammar(
            @ApiParam(value = "Company", required = true) @RequestParam String company) {
        try {
            grammarService.deleteGrammar(company);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new HttpEntity<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/grammar")
    @ApiOperation(value = "Get grammar",
            notes = "Gets the list of prefixes of a given company",
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
    public Object getGrammar(
            @ApiParam(value = "Company", required = true) @RequestParam String company) {
        try {
            Grammar grammar = grammarService.getGrammar(company);
            return grammar;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new HttpEntity<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
