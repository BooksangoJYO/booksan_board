package io.booksan.booksan_board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.booksan.booksan_board.dao.BookDAO;
import io.booksan.booksan_board.dto.BookDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.dto.PageResponseDTO;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BookInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
	private final BookDAO bookDAO;
	private final MapperUtil mapperUtil;
	
	
	@Value("${naver.client.id}")
	private String clientId;
	
	@Value("${naver.client.secret}")
	private String clientSecret;
	
	//자바 코드에서 HTTP요청을 보내기 위한 Spring Framework의 RestTemplate 객체를 생성하고 초기화 하는 부분
	private final RestTemplate restTemplate = new RestTemplate();
	//JSON 파싱용 ObjectMapper 추가
	private final ObjectMapper objectMapper = new ObjectMapper(); 
		
	//책 정보 검색(네이버 검색 api로 책정보 요청)
	public ResponseEntity<PageResponseDTO<BookDTO>> searchBooks(PageRequestDTO pageRequestDTO) {
		//페이지네이션 파라미터 계산
		int start = pageRequestDTO.getSkip() +1; //네이버 API에서 시작 위치는 1부터 시작
		int display = pageRequestDTO.getSize(); //한 페이지에 표시할 결과 개수
		
		//네이버 API URL 설정
		String apiUrl = String.format(
				"https://openapi.naver.com/v1/search/book.json?query=%s&start=%d&display=%d",
				pageRequestDTO.getKeyword(), start, display
		);
		
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
	
		//JSON 응답 파싱
		try {			
			//response.getBody()에서 JSON형식의 응답 본문을 가져와, ObjectMapper의 readTree 메서드를 사용해 JsonNode 형태로 변환
			//JsonNode는 JSON 데이터를 트리 구조로 다룰 수 있게 해주는 Jackson 라이브러리의 클래스입니다.
		    JsonNode root = objectMapper.readTree(response.getBody());
		    //JSON 응답에서 items라는 이름의 노드를 추출
		    //items는 네이버 API의 책 검색 결과에서 개별 책 정보가 담긴 배열
		    JsonNode itemNode = root.path("items");
		    //total 노드를 추출하여 검색 결과의 총 개수를 int로 가져옵니다.
		    int total = root.path("total").asInt();		    
		    
		    // ItemNode를 직접 List<BookDTO>로 변환
		    //ObjectMapper의 convertValue 메서드를 사용하여 JsonNode를 Java 객체 리스트로 변환할수 있습니다.
		    //constructCollectionType 메서드는 변환할 타입이 List<BookDTO>임을 지정해줍니다.
		    List<BookDTO> bookList = objectMapper.convertValue(
		        itemNode, 
		        objectMapper.getTypeFactory().constructCollectionType(List.class, BookDTO.class)
		    );
		    
		    //<BookDTO>는 PageResponseDTO 객체가 BookDTO 타입의 리스트를 포함하도록 타입을 지정하는것
		    PageResponseDTO<BookDTO> pageResponseDTO = PageResponseDTO.<BookDTO>withAll()
		        .pageRequestDTO(pageRequestDTO)
		        .dtoList(bookList)
		        .total(total)
		        .build();

		    return ResponseEntity.ok(pageResponseDTO);
		} catch (Exception e) {
		    throw new RuntimeException("네이버 API로부터 파싱 실패", e);
		}
	
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
