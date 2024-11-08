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
//		private final String CLIENT_ID = "YOUR_CLIENT_ID";
//		private final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";
		
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
		


}
