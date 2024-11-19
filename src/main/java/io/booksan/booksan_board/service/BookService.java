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
import io.booksan.booksan_board.dto.BookCategoryDTO;
import io.booksan.booksan_board.dto.BookCommentDTO;
import io.booksan.booksan_board.dto.BookDTO;
import io.booksan.booksan_board.dto.BookInfoDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.dto.PageResponseDTO;
import io.booksan.booksan_board.dto.RequestDTO;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BookCommentVO;
import io.booksan.booksan_board.vo.BookVO;
import io.booksan.booksan_board.vo.FavoriteBookVO;
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

    @Value("${naver.api.url}")
    private String naverApiUrl;

    //자바 코드에서 HTTP요청을 보내기 위한 Spring Framework의 RestTemplate 객체를 생성하고 초기화 하는 부분
    private final RestTemplate restTemplate = new RestTemplate();
    //JSON 파싱용 ObjectMapper 추가
    private final ObjectMapper objectMapper = new ObjectMapper();

    //책 정보 검색(네이버 검색 api로 책정보 요청)
    public ResponseEntity<PageResponseDTO<BookInfoDTO>> searchBooks(PageRequestDTO pageRequestDTO) {
        //페이지네이션 파라미터 계산
        int start = pageRequestDTO.getSkip() + 1; //네이버 API에서 시작 위치는 1부터 시작
        int display = pageRequestDTO.getSize(); //한 페이지에 표시할 결과 개수

        //네이버 API URL 설정
        String apiUrl = String.format(
                "%s?query=%s&start=%d&display=%d",
                naverApiUrl, pageRequestDTO.getKeyword(), start, display
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
            log.info(response.getBody());
            JsonNode root = objectMapper.readTree(response.getBody());
            //JSON 응답에서 items라는 이름의 노드를 추출
            //items는 네이버 API의 책 검색 결과에서 개별 책 정보가 담긴 배열
            JsonNode itemNode = root.path("items");
            //total 노드를 추출하여 검색 결과의 총 개수를 int로 가져옵니다.
            int total = root.path("total").asInt();

            // ItemNode를 직접 List<BookDTO>로 변환
            //ObjectMapper의 convertValue 메서드를 사용하여 JsonNode를 Java 객체 리스트로 변환할수 있습니다.
            //constructCollectionType 메서드는 변환할 타입이 List<BookDTO>임을 지정해줍니다.
            List<BookInfoDTO> bookList = objectMapper.convertValue(
                    itemNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, BookInfoDTO.class)
            );
            log.info(bookList.get(0).toString());
            //<BookInfoDTO>는 PageResponseDTO 객체가 BookInfoDTO 타입의 리스트를 포함하도록 타입을 지정하는것
            PageResponseDTO<BookInfoDTO> pageResponseDTO = PageResponseDTO.<BookInfoDTO>withAll()
                    .pageRequestDTO(pageRequestDTO)
                    .dtoList(bookList)
                    .total(total)
                    .build();

            return ResponseEntity.ok(pageResponseDTO);
        } catch (Exception e) {
            throw new RuntimeException("네이버 API로부터 파싱 실패", e);
        }

    }

    //게시물 조회시 isbn으로 책정보 검색
    public BookInfoDTO searchBook(String isbn) {
        //네이버 API URL 설정
        try {
            String apiUrl = String.format(
                    "https://openapi.naver.com/v1/search/book.json?query=%s&start=1&display=1",
                    isbn
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
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode itemNode = root.path("items");
            List<BookInfoDTO> bookList = objectMapper.convertValue(
                    itemNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, BookInfoDTO.class)
            );

            return bookList.get(0);
        } catch (Exception e) {
            throw new RuntimeException("네이버 API로부터 파싱 실패", e);
        }
    }

    //카테고리 목록 가져오기
    public List<BookCategoryDTO> getCategories() {
        return bookDAO.getCategories().stream().map(category -> mapperUtil.map(category, BookCategoryDTO.class)).toList();
    }

    //게시물 등록시 책정보 등록
    public int insertBookInfo(RequestDTO requestDTO) {
        int isISBNExists = isISBNExists(requestDTO.getIsbn());

        //isISBNExists가 0이면 책정보가 없는 것이므로 책정보 등록
        if (isISBNExists == 0) {
            BookVO bookVO = new BookVO();
            bookVO.setBookTitle(requestDTO.getBookTitle());
            bookVO.setBookWriter(requestDTO.getBookWriter());
            bookVO.setBookPublisher(requestDTO.getBookPublisher());
            bookVO.setBookImageUrl(requestDTO.getBookImageUrl());
            bookVO.setIsbn(requestDTO.getIsbn());

            return bookDAO.insertBookInfo(bookVO);
        }

        return 0;
    }

    //책정보 등록하기전에 책정보테이블에서 ISBN이 있는지 확인
    public int isISBNExists(String isbn) {
        return bookDAO.isISBNExists(isbn);
    }

    //책 평가 목록 가져오기
    public List<BookCommentDTO> getCommentList(String isbn) {

        return bookDAO.getComment(isbn);
    }

    //책 평가 댓글 등록
    public int insertBookComment(BookCommentVO bookCommentVO) {

        return bookDAO.insertComment(bookCommentVO);
    }

    //책 평가 댓글 수정
    public int updateBookComment(BookCommentVO bookCommentVO) {
        return bookDAO.updateComment(bookCommentVO);
    }

    //책 평가 댓글 삭제
    public int deleteComment(int commentId) {
        return bookDAO.deleteComment(commentId);
    }

    public PageResponseDTO<BookDTO> getFavoriteBookList(PageRequestDTO pageRequestDTO) {
        //DAO에서 게시물 목록을 가져오고, BoardVO를 BoardDTO로 변환
        List<BookDTO> favoriteBookList = bookDAO.getFavoriteBookList(pageRequestDTO).stream().map(book -> mapperUtil.map(book, BookDTO.class)).toList();
        //PageResponseDTO를 생성하여 반환
        return PageResponseDTO.<BookDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(favoriteBookList)
                .total(bookDAO.getFavoriteBookCount(pageRequestDTO))
                .build();
    }

    public int insertFavoriteBook(String isbn, String email) {
        return bookDAO.insertFavoriteBook(new FavoriteBookVO(isbn, email));
    }

	public double getRecommendPrice(double originalPrice, int publishYear, int publishMonth) {
		int currentYear = java.time.Year.now().getValue();
		int currentMonth = java.time.LocalDate.now().getMonthValue();
		
		//경과 월 계산
		int yearsSincePublish = currentYear - publishYear; //년도 차이
		int monthsSincePublish = (yearsSincePublish *12) + (currentMonth - publishMonth);
		
		//감가 계산
		double depreciationRatePerMonth = 0.008; // 매월 10% 감소
		double minimumPriceRate = 0.5; // 최소 50% 보장
		
		//감가 가격 적용
		double depreciatedPrice = originalPrice * Math.pow((1-depreciationRatePerMonth), monthsSincePublish);
		
		//최소 거래가 보장
		double minimumPrice = originalPrice * minimumPriceRate;
		
		return Math.max(depreciatedPrice, minimumPrice); //최소 가격 보장		
	}

}
