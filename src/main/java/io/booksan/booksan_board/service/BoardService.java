package io.booksan.booksan_board.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.booksan.booksan_board.dao.BoardDAO;
import io.booksan.booksan_board.dto.BoardDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BoardVO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
		private final BoardDAO boardDAO;
		private final MapperUtil mapperUtil;
		private final String CLIENT_ID = "YOUR_CLIENT_ID";
		private final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";
		
		//게시물 등록
		public int insertBoard(BoardVO boardVO) {
			return boardDAO.insertBoard(boardVO);
		
		}
		
		//게시물 조회
		public BoardVO readBoardById(int dealId) {
			
			return boardDAO.readBoardById(dealId);
		}

		//게시물 목록
		public List<BoardDTO> getBoardList(PageRequestDTO pageRequestDTO) {
			return boardDAO.getBoardList(pageRequestDTO).stream().map(board -> mapperUtil.map(board, BoardDTO.class)).toList();
		}

		//게시물 수정
		public int updateBoard(BoardVO boardVO) {
			
			return boardDAO.updateBoard(boardVO);
		}

		//게시물 삭제
		public int deleteBoard(int dealId) {
			
			return boardDAO.deleteBoard(dealId);
		}
		
		
		//
		public String getBookInfoByIsbn(String isbn) {
	        // 네이버 책 검색 API URL
	        String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + isbn;

	        // RestTemplate 객체 생성
	        RestTemplate restTemplate = new RestTemplate();

	        // HTTP 헤더 설정
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("X-Naver-Client-Id", CLIENT_ID);
	        headers.set("X-Naver-Client-Secret", CLIENT_SECRET);

	        // 요청 엔티티 생성
	        HttpEntity<String> entity = new HttpEntity<>(headers);

	        // 네이버 API에 요청 보내기
	        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

	        // API 응답 반환
	        return response.getBody();
	    }

}
