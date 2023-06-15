package com.board.project.repository;

import com.board.project.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

//    @EntityGraph(attributePaths = {"roleset"},
//               type = EntityGraph.EntityGraphType.LOAD)
//    @Query("select m from Member m where m.fromSocial =:fromSocial and m.email =:email")
//    Optional<Member> findByEmail(@Param("email") String email, @Param("fromSocial") boolean social);

    @Query("select m from Member m where m.loginId =:loginId")
    Member findByLoginId(@Param("loginId") String loginId);
    
    @Query("select m from Member m where m.email =:email")
    Member findByEmail(@Param("email") String email);


}