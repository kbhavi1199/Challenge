package com.code.challenge.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Releases {

	private String description;

	private OrganizationDate date;

	private double laborHours;

	private String name;

	private String organization;

	private Permissions permissions;

	private String status;

}
