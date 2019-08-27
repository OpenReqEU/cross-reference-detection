package com.essi.dependency.controller;

import com.essi.dependency.components.Grammar;
import com.essi.dependency.service.GrammarService;
import com.essi.dependency.util.Control;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/upc/cross-reference-detection")
@Api(value = "ReqPrefixControllerAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReqPrefixController {

    @Autowired
    private GrammarService grammarService;

    //TODO none of the next methods are throwing other type of status than 200 or 500. Where are the 401, 403...?????????????????????????????

    @PostMapping("/reqPrefix")
    @ApiOperation(value = "Store prefixes",
            notes = "Given a company, stores the list of prefixes used to identify requirements, which will be used in the grammar rules to extract requirement cross-references.",
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
    public ResponseEntity uploadGrammar(
            @ApiParam(value = "The list of prefixes",
                    required = true) @RequestBody Grammar prefixes,
            @ApiParam(value = "Company", required = true) @RequestParam("company") String company) {
        try {
            grammarService.uploadGrammar(company, prefixes);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            return new ResponseEntity<>(new HttpEntity<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/reqPrefix")
    @ApiOperation(value = "Update prefixes",
            notes = "Given a company, updates its existing list of prefixes used to identify requirements, which will be used in the grammar rules to extract requirement cross-references",
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
    public ResponseEntity updateGrammar(
            @ApiParam(value = "The list of prefixes",
                    required = true) @RequestBody Grammar prefixes,
            @ApiParam(value = "Company", required = true) @RequestParam("company") String company) {
        try {
            grammarService.updateGrammar(company, prefixes);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            return new ResponseEntity<>(new HttpEntity<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/reqPrefix")
    @ApiOperation(value = "Delete prefixes",
            notes = "Deletes the prefixes of a given company",
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
    public ResponseEntity deleteGrammar(
            @ApiParam(value = "Company", required = true) @RequestParam String company) {
        try {
            grammarService.deleteGrammar(company);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            return new ResponseEntity<>(new HttpEntity<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/grammar")
    @ApiOperation(value = "Get prefixes",
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
            return grammarService.getGrammar(company);
        } catch (Exception e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            return new ResponseEntity<>(new HttpEntity<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
