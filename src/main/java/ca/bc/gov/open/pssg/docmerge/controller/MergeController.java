package ca.bc.gov.open.pssg.docmerge.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.open.pssg.docmerge.exception.MergeException;
import ca.bc.gov.open.pssg.docmerge.model.JSONResponse;
import ca.bc.gov.open.pssg.docmerge.model.PDFMergeRequest;
import ca.bc.gov.open.pssg.docmerge.model.PDFMergeResponse;
import ca.bc.gov.open.pssg.docmerge.service.MergeService;
import ca.bc.gov.open.pssg.docmerge.utils.PDFMergeConstants;
import ca.bc.gov.open.pssg.docmerge.utils.PDFMergeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Main RESTful controller. 
 * 
 * @author shaunmillargov
 *
 */

@RestController
public class MergeController {
	
	@Autowired
	private MergeService mergeService;
	
	private final Logger logger = LoggerFactory.getLogger(MergeController.class);

	@PostMapping(value = {"/merge/{correlationId}" }, 
			consumes = PDFMergeConstants.JSON_CONTENT, 
			produces = PDFMergeConstants.JSON_CONTENT)
	public ResponseEntity<JSONResponse<PDFMergeResponse>> mergeDocumentPost(
			@PathVariable(value = "correlationId", required = true) String correlationId, 
			@Valid @RequestBody(required = true)  PDFMergeRequest request)  {
		
		logger.info("Starting merge process...");
		
		try {
			
			PDFMergeResponse mergResp = mergeService.mergePDFDocuments(request, correlationId);
			JSONResponse<PDFMergeResponse> resp = new JSONResponse<>(mergResp);
			logger.info("Merge process complete.");
			return new ResponseEntity<>(resp, HttpStatus.OK);
			
		} catch (MergeException e) {
			
			e.printStackTrace();
			logger.error("Document Merge encountered an error " + e.getMessage());
			return new ResponseEntity<>(
					PDFMergeUtils.buildErrorResponse(String.format(PDFMergeConstants.NOT_PROCESSED_ERROR, correlationId), 404),
					HttpStatus.NOT_FOUND);
		}
	}

}