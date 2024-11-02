package io.booksan.booksan_board.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.booksan.booksan_board.service.BookService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class NaverBookController {
	
	private final BookService bookService;	
	
	@GetMapping("/search")
	public ResponseEntity<?> searchBooks(@RequestParam String query){
		//응답반환
		return bookService.searchBooks(query);
	}
}
