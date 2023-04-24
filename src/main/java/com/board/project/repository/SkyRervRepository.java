package com.board.project.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.project.entity.SkyRerv;

public interface SkyRervRepository extends JpaRepository<SkyRerv, Long> {

	 List<SkyRerv> findByUserNameAndPassport(String userName, String passport);
	 
	 
}
