package com.board.project.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkySearchResultDTO {

	private String airLineName;
	private String logo;
	private String price;
	private String duration;
	private String depDate;
	private String depCode;
	private String arrDate;
	private String arrCode;
	private String viaCode;
	private String viaDateTime;
	
}

