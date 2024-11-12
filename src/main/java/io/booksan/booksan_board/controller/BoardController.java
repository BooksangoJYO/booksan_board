package io.booksan.booksan_board.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_board.dto.BoardDTO;
import io.booksan.booksan_board.dto.BookCommentDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.dto.PageResponseDTO;
import io.booksan.booksan_board.dto.BookDTO;
import io.booksan.booksan_board.dto.BookInfoDTO;
import io.booksan.booksan_board.dto.RequestDTO;

import io.booksan.booksan_board.service.BoardService;
import io.booksan.booksan_board.service.BookService;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BoardVO;
import io.booksan.booksan_board.vo.BookVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
	private final MapperUtil mapperUtil;
	private final BoardService boardService;
	private final BookService bookService;
	
	
	//게시물 등록
	@PostMapping("/insert")
	public ResponseEntity<?> insertBoard(@RequestBody RequestDTO requestDTO) {
		log.info("Request Data: {}", requestDTO);
		//게시물 등록 정보
		BoardDTO boardDTO = new BoardDTO();
		
		//Setter 메서드를 사용하여 필드값 설정(게시물 등록폼에서 얻어온 데이터 세팅)
		boardDTO.setTitle(requestDTO.getTitle());		
		boardDTO.setContent(requestDTO.getContent());
		boardDTO.setBooksCategoryId(requestDTO.getBooksCategoryId());
		boardDTO.setPrice(requestDTO.getPrice());
		boardDTO.setEmail(requestDTO.getEmail());
		boardDTO.setIsbn(requestDTO.getIsbn());
		
		//책 정보 설정
		BookDTO bookDTO = new BookDTO();
		bookDTO.setBookTitle(requestDTO.getBookTitle());
		bookDTO.setBookWriter(requestDTO.getBookWriter());
		bookDTO.setBookPublisher(requestDTO.getBookPublisher());
		bookDTO.setIsbn(requestDTO.getIsbn());
		
		//ISBN 존재 여부 확인(게시물 등록시 책정보테이블에 없는 ISBN인경우 책등록 요청)
		if(bookService.isISBNExists(requestDTO.getIsbn())==0) {
			int bookResult=bookService.insertBookInfo(mapperUtil.map(bookDTO, BookVO.class));
		}
		
		//책정보 등록이 먼저 등록이 되어야 게시물 등록할때 책정보테이블의 isbn를 참조할수 있음
		int boardResult=boardService.insertBoard(mapperUtil.map(boardDTO, BoardVO.class));		
		
		//응답 데이터를 저장할 Map 생성
		Map<String, Object> response = new HashMap<>();
		
		//boardResult가 1일경우 게시물 등록성공, 1이 아닌경우 게시물 등록 실패
		if(boardResult==1) {
			response.put("status", "success");
			response.put("message", "게시물 등록 성공");
			return ResponseEntity.ok(response);
		}else {
			response.put("status", "fail");
			response.put("message", "게시물 등록 실패");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}		
	}
	
	//게시물 단건조회
	@GetMapping("/read/{dealId}") 
	public ResponseEntity<?> readBoard(@PathVariable("dealId") int dealId){
		
		
		//단건조회 결과 boardVO에 담음
		BoardDTO boardDTO = boardService.readBoardById(dealId);
		
		
		//응답 데이터 저장할 Map
		Map<String, Object> response = new HashMap<>();
		
		if(boardDTO != null) {			
			response.put("status", "success");
			response.put("data", boardDTO);
			return ResponseEntity.ok(response);
		} else {
			response.put("stauts", "fail");
			response.put("message", "게시물을 찾을 수 없습니다.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		
	}
	
	//게시판 목록
	@GetMapping("/list")
	public ResponseEntity<?> getBoardList(PageRequestDTO pageRequestDTO){		

		log.info(pageRequestDTO.toString());
		//게시물 목록 가져와서 boadList에 담기
		PageResponseDTO<BoardDTO> boardList = boardService.getBoardList(pageRequestDTO);
		
		
		//응답데이터 저장할 Map
		Map<String,Object> response = new HashMap<>();
		log.info( boardList.toString());
		//게시물이 있는 경우
		if(!boardList.getDtoList().isEmpty()) {
			response.put("status", "success");
			response.put("data", boardList);
			return ResponseEntity.ok(response);
		} else {
			//게시물이 없는 경우
			response.put("status", "fail");
			response.put("message", "게시물이 없습니다.");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		}
	}
	
	//게시판 수정
	@PutMapping("/update")
	public ResponseEntity<?> updateBoard(@RequestBody BoardDTO boardDTO) {
		
		int result = boardService.updateBoard(mapperUtil.map(boardDTO, BoardVO.class));	
		
		//응답 데이터를 저장할 response
		Map<String, Object> response = new HashMap<>();
		
		if(result ==1) {
			response.put("status", "success");
			response.put("message", "게시물 수정 성공");
			return ResponseEntity.ok(response);
		}  else {
			response.put("status", "fail");
			response.put("message", "게시물 수정 실패");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
		
	}
	
	
	//게시물 삭제
	@DeleteMapping("/delete/{dealId}")
	public ResponseEntity<?> deleteBoard(@PathVariable("dealId") int dealId){
		
		int result = boardService.deleteBoard(dealId);
		
		//응답데이터 저장할 response
		Map<String,Object> response = new HashMap<>();
		
		if(result ==1) {
			response.put("status", "success");
			response.put("message", "게시물 삭제 성공");
			return ResponseEntity.ok(response);
		} else {
			response.put("status", "fail");
			response.put("message", "게시물 삭제 실패");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	} 

}
