package io.booksan.booksan_board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_board.dto.BookDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.service.BookService;
import io.booksan.booksan_board.util.MapperUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
	private final MapperUtil mapperUtil;
	private final BookService bookService;	
	
	//게시물 등록 페이지에서 키워드로 책정보 검색(네이버 검색 API)
	@GetMapping("/search/{searchTitle}")
	public ResponseEntity<?> searchBooks(
			@PathVariable("searchTitle") String searchTitle,
			@RequestParam(name = "page", defaultValue= "1") int page,
			@RequestParam(name = "size", defaultValue="10") int size
	) {
		//PageRequestDTO 객체 생성
		PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
				.page(page)
				.size(size)
				.keyword(searchTitle) //검색어를 keyword 필드에 설정
				.build();	
		
		//응답반환
		return bookService.searchBooks(pageRequestDTO);
	}
	
	//카테고리 목록 가져오기
	@GetMapping("/categories")
	public ResponseEntity<?> getCategories() {
		//책 카테고리 정보 가져와서 booksCategories에 담기
		List<BookDTO> booksCategories = bookService.getCategories();
		
				
		//응답데이터 저장할 Map
		Map<String, Object> response = new HashMap<>();		
		response.put("status", "success");
		response.put("message", "카테고리 불러오기 성공");
		response.put("booksCategories", booksCategories);
		
		
		return ResponseEntity.ok(response);
	}
}
