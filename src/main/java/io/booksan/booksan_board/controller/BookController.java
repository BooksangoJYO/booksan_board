package io.booksan.booksan_board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_board.dto.BookCategoryDTO;
import io.booksan.booksan_board.dto.BookCommentDTO;
import io.booksan.booksan_board.dto.BookDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.service.BookService;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BookCommentVO;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
	private final MapperUtil mapperUtil;
	private final BookService bookService;	
	
	//게시물 등록 페이지에서 키워드로 책정보 검색(네이버 검색 API)
	@GetMapping("/searchAll/{searchTitle}")
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
	
	
	//게시물 조회페이지에서 isbn으로 책정보 단건조회(네이버 검색 api)
	@GetMapping("/search/{isbn}")
		public ResponseEntity<?> searchBook(@PathVariable("isbn") String isbn) {
			return bookService.searchBooks(isbn);
	}
	

	//카테고리 목록 가져오기
	@GetMapping("/categories")
	public ResponseEntity<?> getCategories() {
		//책 카테고리 정보 가져와서 booksCategories에 담기
		List<BookCategoryDTO> booksCategories = bookService.getCategories();
		
				
		//응답데이터 저장할 Map
		Map<String, Object> response = new HashMap<>();		
		response.put("status", "success");
		response.put("message", "카테고리 불러오기 성공");
		response.put("booksCategories", booksCategories);
		
		
		return ResponseEntity.ok(response);
	}
	
	//책 평가 댓글 등록
	@PostMapping("/comment/insert")
	public ResponseEntity<?> insertBookComment(@RequestBody BookCommentDTO bookCommentDTO) {
		//댓글 등록
		int result = bookService.insertBookComment(mapperUtil.map(bookCommentDTO, BookCommentVO.class));
	
		//응답 데이터 생성
		Map<String, Object> response = new HashMap<>();
		if (result==1) {
			response.put("status", "success");
			response.put("message", "댓글 등록 성공");
			return ResponseEntity.ok(response);
		} else {
			response.put("status", "fail");
			response.put("message", "댓글 등록 실패");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}	
	}
	
	//책 평가 댓글 수정
	@PutMapping("/comment/update")
	public ResponseEntity<?> updateBookComment(@RequestBody BookCommentDTO bookCommentDTO) {
		//댓글 수정
		int result = bookService.updateBookComment(mapperUtil.map(bookCommentDTO, BookCommentVO.class));
		
		//응답 데이터 생성
		Map<String, Object> response = new HashMap<>();
		if(result==1) {
			response.put("status", "success");
			response.put("message", "책 평가 댓글 수정 성공");
			return ResponseEntity.ok(response);
		} else {
			response.put("status", "fail");
			response.put("message", "책 평가 댓글 수정 실패");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	//책 평가 댓글 삭제
	@DeleteMapping("/comment/delete/{commentId}")
	public ResponseEntity<?> deleteBookComment(@PathVariable("commentId") int commentId) {
		int result = bookService.deleteComment(commentId);
		
		//응답 데이터
		Map<String, Object> response = new HashMap<>();
		
		if(result ==1) {
			response.put("status", "success");
			response.put("message", "댓글 삭제 성공");
			return ResponseEntity.ok(response);
		} else {
			response.put("status", "fail");
			response.put("message", "댓글 삭제 실패");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
}
