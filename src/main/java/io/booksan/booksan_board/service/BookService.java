package io.booksan.booksan_board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import io.booksan.booksan_board.dao.BookDAO;
import io.booksan.booksan_board.dto.BookDTO;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BookInfoVO;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BookService {
	private final BookDAO bookDAO;
	private final MapperUtil mapperUtil;
	
	
	@Value("${naver.client.id}")
	private String clientId;
	
	@Value("${naver.client.secret}")
	private String clientSecret;
	
	//자바 코드에서 HTTP요청을 보내기 위한 Spring Framework의 RestTemplate 객체를 생성하고 초기화 하는 부분
	private final RestTemplate restTemplate = new RestTemplate();
		
	
	public ResponseEntity<String> searchBooks(String query){
		//네이버 API URL 설정
		String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + query;
		
		//HttpHeaders 설정
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Naver-Client-Id", clientId);
		headers.set("X-Naver-Client-Secret", clientSecret);
		
		//HttpEntity 객체 생성( 본문은 필요 없으므로 void 사용)
		HttpEntity<String> entity = new HttpEntity<>(headers);
		
		//API 요청 및 응답 받기
		ResponseEntity<String> response = restTemplate.exchange(
			apiUrl,
			HttpMethod.GET,
			entity,
			String.class					
		);
	
		return restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
	
	}

	//카테고리 목록 가져오기
	public List<BookDTO> getCategories() {		
		return bookDAO.getCategories().stream().map(category -> mapperUtil.map(category, BookDTO.class)).toList();
	}

	//게시물 등록시 책정보 등록
	public int insertBookInfo(BookInfoVO bookInfoVO) {
		return bookDAO.insertBookInfo(bookInfoVO);
	}
	
	//책정보 등록하기전에 책정보테이블에서 ISBN이 있는지 확인
	public int isISBNExists(String isbn) {		
		return bookDAO.isISBNExists(isbn);
	}
	
	
}
