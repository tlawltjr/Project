package com.board.project.service;

import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.board.project.dto.BoardDTO;
import com.board.project.dto.MemberAdapter;
import com.board.project.dto.PageRequestDTO;
import com.board.project.dto.PageResultDTO;
import com.board.project.entity.Board;
import com.board.project.entity.Member;
import com.board.project.entity.QBoard;
import com.board.project.entity.QMember;
import com.board.project.repository.BoardRepository;
import com.board.project.repository.MemberRepository;
import com.nimbusds.jose.proc.SecurityContext;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
@Service
@RequiredArgsConstructor
@Log4j2
public class BoardServiceImpl implements BoardService{

	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;
	
	@Override
	@Transactional
	public void register(BoardDTO dto) {
		 log.info("신규등록 호출");
		    
		 System.out.println("dto.getid"+dto.getLoginId());
		 
		    Member member = memberRepository.findById(dto.getLoginId()).orElse(null);
		    if(member == null) {
		        // 에러 처리
		    	System.out.println("에러 null");
		    }
		    
		    Board board = dtoToEntity(dto);
		    board.setLoginId(member);
		    
		    boardRepository.save(board);
	}
	@Override
	public PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO requestDTO) {
	    Pageable pageable = requestDTO.getPageable(Sort.by("bno").descending());
	    
	    BooleanBuilder booleanBuilder = getFind(requestDTO);
	    
	    Page<Object[]> result = boardRepository.findAll(booleanBuilder, pageable)
	    		.map(board -> new Object[]{board, board.getLoginId()});

	    Function<Object[], BoardDTO> fn = (entity->entityToDto((Board)entity[0], (Member)entity[1]));
	    
	    System.out.println(fn.toString());
	    

	    return new PageResultDTO<>(result, fn);
	}
	
	
	@Override
	public BoardDTO read(Long bno) {
		
		Object ob = boardRepository.getBoardByBno(bno);
		Object[] arr  = (Object[]) ob;
		
		return entityToDto((Board)arr[0], (Member)arr[1]);
	}
	
	@Transactional
	@Override
	public void remove(Long bno) {
		
		boardRepository.deleteById(bno);
		
		
	}
	
	@Transactional
    @Override
    public void modify(BoardDTO boardDTO) {

        Board board = boardRepository.getOne(boardDTO.getBno());

        if(board != null) {

            board.changeTitle(boardDTO.getTitle());
            board.changeContent(boardDTO.getContent());

            boardRepository.save(board);
        }
    }
	//검색 조건을 추가하여, 검색에 매칭되는 Entity를 구성해서 getListPage()로 보낸다
		//QueryDSL을 이용할 예정이라 리턴타입은 javax.persistant.page 객체로 
		//리턴시키기 위해서 BooleanBuilder 객체로 리턴할 예정
		//이렇게 리턴된 BooleanBuilder를 findAll(BooleanBuilder, Pageable)
		//메서드를 통해 Page객체를 얻어내서 list.html 까지 전달 시킨다.
		//QueryDSL의 장점 : Entity 필드를 조회 조건으로 이용할 수 있다
	private BooleanBuilder getFind(PageRequestDTO pageRequestDTO) {
			//사용자가 요청한 검색 키워드 알아내기
			String type = pageRequestDTO.getType();//c,w,t 거나 모두이거나
			
			QBoard qBoard = QBoard.board;
			QMember qMember = QMember.member;
			//QBaseEntity a = QBaseEntity.baseEntity;
			String keyword = pageRequestDTO.getKeyword();
			
			BooleanBuilder booleanBuilder = new BooleanBuilder();
			
			//검색조건을 생성하는데, 기본적으로 gno를 기준으로 먼저 검색조건을 생성합니다
			BooleanExpression booleanExpression = qBoard.bno.gt(0L);
			//생성된 조회건을 booleanbuilder에 추가합니다
			//이유는 repository.find()에 들어갈 파라미터는 booleanBuilder 객체이기떄문입니다
			booleanBuilder.and(booleanExpression);
			//만약 검색조건이 아무것도 없다면 , 일반 조회조건 즉, gno > 0인 애들을 돌려주도록 합니다
			if(type == null || type.equals("") || type.isEmpty() || type.length() == 0){
				return booleanBuilder;
			}
			
			//아래서 사용될 조회 조건을 담는 BooleanBuilder 또 하나 생성
			BooleanBuilder findbuilder = new BooleanBuilder();
			
			//어떤 필드(QDomain)에서 keyword를 찾아야 할지 요청 타입(type)을 검색해 봅니다
			//만약 아무런 type 조건이 없을 경우, 그냥 글번호를 기준으로 넘겨주도록 합니다
			if(type.contains("t")) {
				//모든 조건을 or 로 묶어서 추가합니다
				findbuilder.or(qBoard.title.contains(keyword));
			}
			if(type.contains("c")) {
				findbuilder.or(qBoard.content.contains(keyword));
			}
			if(type.contains("w")) {
				findbuilder.or(qMember.loginId.contains(keyword));
			}
			//위 조건 검색을 하나로 통함
			booleanBuilder.and(findbuilder);
			
			return booleanBuilder;
		}
	
	
}
