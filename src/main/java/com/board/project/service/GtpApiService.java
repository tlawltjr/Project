package com.board.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.board.project.dto.GtpDTO;
import com.board.project.dto.GtpDTO2;


public interface GtpApiService {
	public  String callChatGPT(String prompt);
	List<GtpDTO> callblog(GtpDTO2 dto2);
	

	
}
