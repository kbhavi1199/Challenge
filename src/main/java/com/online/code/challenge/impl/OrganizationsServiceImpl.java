package com.online.code.challenge.impl;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.code.challenge.response.EnergyResponse;
import com.code.challenge.response.Licenses;
import com.code.challenge.response.OrganizationReleaseResponse;
import com.code.challenge.response.Organizations;
import com.code.challenge.response.Releases;
import com.code.challenge.service.OrganizationsService;
import com.code.challenge.util.ServiceResponse;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

@Service
public class OrganizationsServiceImpl implements OrganizationsService {

	private static final Logger LOG = LoggerFactory.getLogger(OrganizationsServiceImpl.class);
	private static final String ENERGY_URL = "https://www.energy.gov/sites/prod/files/2020/12/f81/code-12-15-2020.json";

	@Autowired
	RestTemplate restTemplate;

	@Override
	public ServiceResponse<Organizations> getReleaseDetails(String sortBy, String sortAs) {
		ServiceResponse<Organizations> serviceResponse = new ServiceResponse<Organizations>();
		Organizations organizations = new Organizations();
		EnergyResponse energyResponse = restTemplate.getForObject(ENERGY_URL, EnergyResponse.class);
		List<OrganizationReleaseResponse> organizationReleaselise = getOrganizationReleaseList(
				energyResponse.getReleases());
		if ("asc".equalsIgnoreCase(sortAs)) {
			sortInAscending(sortBy, organizationReleaselise);
		} else if ("desc".equalsIgnoreCase(sortAs)) {
			sortInDecending(sortBy, organizationReleaselise);
		}

		organizations.setOrganizations(organizationReleaselise);
		serviceResponse.setPayload(organizations);
		return serviceResponse;
	}

	private void sortInAscending(String sortBy, List<OrganizationReleaseResponse> organizationReleaselise) {
		if ("organization".equalsIgnoreCase(sortBy)) {
			Collections.sort(organizationReleaselise,
					(org1, org2) -> org1.getOrganization().compareTo(org2.getOrganization()));
		} else if ("release_count".equalsIgnoreCase(sortBy)) {
			Collections.sort(organizationReleaselise,
					(org1, org2) -> org1.getRelease_count().compareTo(org2.getRelease_count()));
		} else if ("total_labor_hours".equalsIgnoreCase(sortBy)) {
			Collections.sort(organizationReleaselise,
					(org1, org2) -> org1.getTotal_labor_hours().compareTo(org2.getTotal_labor_hours()));
		}
	}

	private void sortInDecending(String sortBy, List<OrganizationReleaseResponse> organizationReleaselise) {
		if ("organization".equalsIgnoreCase(sortBy)) {
			Collections.sort(organizationReleaselise,
					(org1, org2) -> org2.getOrganization().compareTo(org1.getOrganization()));
		} else if ("release_count".equalsIgnoreCase(sortBy)) {
			Collections.sort(organizationReleaselise,
					(org1, org2) -> org2.getRelease_count().compareTo(org1.getRelease_count()));
		} else if ("total_labor_hours".equalsIgnoreCase(sortBy)) {
			Collections.sort(organizationReleaselise,
					(org1, org2) -> org2.getTotal_labor_hours().compareTo(org1.getTotal_labor_hours()));
		}
	}

	private List<OrganizationReleaseResponse> getOrganizationReleaseList(List<Releases> releaseList) {
		List<OrganizationReleaseResponse> organizationReleaselise = new ArrayList<OrganizationReleaseResponse>();
		Map<String, List<Releases>> releaseListGroupByOrgName = releaseList.stream()
				.collect(Collectors.groupingBy(Releases::getOrganization));
		for (Map.Entry<String, List<Releases>> releaseMap : releaseListGroupByOrgName.entrySet()) {
			double totalHours = 0;
			Boolean isProduction = true;
			List<String> licenses = new ArrayList<String>();
			Map<String, Integer> monthOccurancesMap = new HashMap<>();
			for (Releases releases : releaseMap.getValue()) {
				if (!"Production".equals(releases.getStatus())) {
					isProduction = false;
				}
				totalHours = totalHours + releases.getLaborHours();
				licenses.addAll(releases.getPermissions().getLicenses().stream().map(Licenses::getName)
						.collect(Collectors.toList()));
				String createdMonth = releases.getDate().getCreated().split("-")[1];
				if (monthOccurancesMap.containsKey(createdMonth)) {
					monthOccurancesMap.put(createdMonth, monthOccurancesMap.get(createdMonth) + 1);
				} else {
					monthOccurancesMap.put(createdMonth, 1);
				}

			}
			int maxMonthOccurance = (Collections.max(monthOccurancesMap.values()));
			List<Integer> monthList = new ArrayList<Integer>();
			monthList.add(Integer.valueOf(getKey(monthOccurancesMap, maxMonthOccurance)));

			organizationReleaselise.add(
					new OrganizationReleaseResponse(releaseMap.getKey(), releaseMap.getValue().size(), (int) totalHours,
							isProduction, licenses.stream().distinct().collect(Collectors.toList()), monthList));
		}
		return organizationReleaselise;
	}

	public static <K, V> K getKey(Map<K, V> monthOccurancesMap, V maxMonthOccurance) {
		return monthOccurancesMap.entrySet().stream().filter(entry -> maxMonthOccurance.equals(entry.getValue()))
				.map(Map.Entry::getKey).findFirst().get();
	}

	@Override
	public ServiceResponse<String> downloadReleaseDetailsCsv(String filePath) {
		ServiceResponse<String> serviceResponse = new ServiceResponse<>();
		EnergyResponse energyResponse = restTemplate.getForObject(ENERGY_URL, EnergyResponse.class);
		List<OrganizationReleaseResponse> organizationReleaselist = getOrganizationReleaseList(
				energyResponse.getReleases());
		try {
			FileWriter writer = new FileWriter(filePath);
			ColumnPositionMappingStrategy ColPosMappingStrategy = new ColumnPositionMappingStrategy();
			ColPosMappingStrategy.setType(OrganizationReleaseResponse.class);

			// Arrange column name as provided in below array.
			String[] columns = new String[] { "organization", "release_count", "total_labor_hours", "all_in_production",
					"licenses", "most_active_months" };
			ColPosMappingStrategy.setColumnMapping(columns);

			// Createing StatefulBeanToCsv object
			StatefulBeanToCsvBuilder<OrganizationReleaseResponse> builder = new StatefulBeanToCsvBuilder(writer);
			StatefulBeanToCsv csvWriter = builder.withMappingStrategy(ColPosMappingStrategy).build();

			// Write list to StatefulBeanToCsv object
			csvWriter.write(organizationReleaselist);

			// closing the writer object
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serviceResponse.setPayload("{\"Status\":\"success\"}");
		return serviceResponse;
	}

}
