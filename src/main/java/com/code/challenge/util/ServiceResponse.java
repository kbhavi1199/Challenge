package com.code.challenge.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceResponse<T> {

	private T payload;
	private int businessCode;
	private String extraDetails;
}