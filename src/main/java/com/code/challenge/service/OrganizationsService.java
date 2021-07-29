package com.code.challenge.service;

import com.code.challenge.response.Organizations;
import com.code.challenge.util.ServiceResponse;

public interface OrganizationsService {

	ServiceResponse<Organizations> getReleaseDetails(String sortBy, String sortAs);

	ServiceResponse<String> downloadReleaseDetailsCsv(String filePath);

}
