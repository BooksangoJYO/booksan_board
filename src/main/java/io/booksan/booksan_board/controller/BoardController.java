package io.booksan.booksan_board.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_board.dto.BoardDTO;
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
	
	
	@PostMapping("/insert")
	public ResponseEntity<?> insertBoard(@RequestBody BoardDTO boardDTO) {
		int result=boardService.insertBoard(mapperUtil.map(boardDTO, BoardVO.class));
		
		//응답 데이터를 저장할 Map 생성
		Map<String, Object> response = new HashMap<>();
		
		//result가 1일경우 게시물 등록성공, 1이 아닌경우 게시물 등록 실패
		if(result==1) {
			response.put("status", "success");
			response.put("message", "게시물 등록 성공");
			
		}else {
			response.put("status", "fail");
			response.put("message", "게시물 등록 실패");			
		}
		
		return ResponseEntity.ok(response);
	}
 
}
