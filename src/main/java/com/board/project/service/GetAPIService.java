package com.board.project.service;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.board.project.dto.MovieSearchDTO;
import com.board.project.dto.WLjmtDTO;

@Service
public interface GetAPIService {

	
	void getAPI(String search, Model model) throws ParseException,KeyManagementException, NoSuchAlgorithmException, KeyStoreException;
	
	List<MovieSearchDTO> getMovieSearchAPI(String search) throws ParseException;
	
	void getMovieAPI(String search, Model model) throws ParseException;
		
	
}
