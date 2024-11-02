package io.booksan.booksan_board.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_board.dto.BoardDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.service.BoardService;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BoardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
	private final MapperUtil mapperUtil;
	private final BoardService boardService;
	
	//게시물 등록
	@PostMapping("/insert")
	public ResponseEntity<?> insertBoard(@RequestBody BoardDTO boardDTO) {
		int result=boardService.insertBoard(mapperUtil.map(boardDTO, BoardVO.class));
		
		//응답 데이터를 저장할 Map 생성
		Map<String, Object> response = new HashMap<>();
		
		//result가 1일경우 게시물 등록성공, 1이 아닌경우 게시물 등록 실패
		if(result==1) {
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
		BoardVO boardVO = boardService.readBoardById(dealId);
		
		
		//응답 데이터 저장할 Map
		Map<String, Object> response = new HashMap<>();
		
		if(boardVO != null) {
			response.put("status", "success");
			response.put("data", boardVO);
			return ResponseEntity.ok(response);
		} else {
			response.put("stauts", "fail");
			response.put("message", "게시물을 찾을 수 없습니다.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		
	}
	
	//게시판 목록
	@GetMapping("/list")
	public ResponseEntity<?> getBoardList(@RequestBody PageRequestDTO pageRequestDTO){
		//게시물 목록 가져와서 boadList에 담기
		List<BoardDTO> boardList = boardService.getBoardList();
		
		
		//응답데이터 저장할 Map
		Map<String,Object> response = new HashMap<>();
		
		//게시물이 있는 경우
		if(!boardList.isEmpty()) {
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
	
	//책정보 얻어오기(네이버 오픈 API 호출)
	@GetMapping("book/{isbn}")
    public ResponseEntity<?> searchBookByIsbn(@PathVariable String isbn) {
        String bookInfo = boardService.getBookInfoByIsbn(isbn);
        return ResponseEntity.ok(bookInfo);
    }
}
