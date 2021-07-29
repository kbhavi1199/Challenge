package com.code.challenge.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationReleaseResponse {

	private String organization;

	private Integer release_count;

	private Integer total_labor_hours;

	private Boolean all_in_production;

	private List<String> licenses;

	private List<Integer> most_active_months;

}
