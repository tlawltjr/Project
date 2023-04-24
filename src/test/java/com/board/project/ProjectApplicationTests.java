package com.board.project;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.board.project.entity.Board;
import com.board.project.entity.Member;
import com.board.project.repository.BoardRepository;

@SpringBootTest
class ProjectApplicationTests {

	@Autowired
	private BoardRepository boardRepository;
	
    @Test
    void contextLoads() {
    	
    	
    }

}
