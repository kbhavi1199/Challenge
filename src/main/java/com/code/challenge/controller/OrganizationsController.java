package com.code.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code.challenge.exception.ApiRequestException;
import com.code.challenge.response.Organizations;
import com.code.challenge.service.OrganizationsService;
import com.code.challenge.util.ServiceResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;

@RestController
public class OrganizationsController {

	@Autowired
	private OrganizationsService organizationsService;

	@ApiOperation(value = "List All the Organizations Release Details", authorizations = @Authorization(value = "Authorization", scopes = @AuthorizationScope(description = "write", scope = "write")))
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully got the list of Major Achievements for students"),
			@ApiResponse(code = 400, message = "Bad request that might happen because of header mismatch"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "There is some error occurred and not handled") })
	@GetMapping("/release-details")
	public ResponseEntity<Organizations> getReleaseDetails(@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(value = "sortAs", required = false, defaultValue = "asc") String sortAs) throws ApiRequestException {
		ServiceResponse<Organizations> serviceResponse = organizationsService.getReleaseDetails(sortBy,
				sortAs);
		return ResponseEntity.status(HttpStatus.OK).body(serviceResponse.getPayload());

	}

	@ApiOperation(value = "List All the Organizations Release Details", authorizations = @Authorization(value = "Authorization", scopes = @AuthorizationScope(description = "write", scope = "write")))
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully got the list of Major Achievements for students"),
			@ApiResponse(code = 400, message = "Bad request that might happen because of header mismatch"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "There is some error occurred and not handled") })
	@GetMapping("/release-details/downloads")
	public ResponseEntity<String> getReleaseDetailsCsv(@RequestParam(value = "filePath", required = true) String filePath)
			throws ApiRequestException {
		ServiceResponse<String> data = organizationsService
				.downloadReleaseDetailsCsv(filePath);
		return ResponseEntity.status(HttpStatus.OK).body(data.getPayload());

	}

}
