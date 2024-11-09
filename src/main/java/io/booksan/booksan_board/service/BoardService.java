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
import io.booksan.booksan_board.dto.PageResponseDTO;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BoardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
		private final BoardDAO boardDAO;
		private final MapperUtil mapperUtil;
//		private final String CLIENT_ID = "YOUR_CLIENT_ID";
//		private final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";
		
		//게시물 등록
		public int insertBoard(BoardVO boardVO) {
			return boardDAO.insertBoard(boardVO);
		
		}
		
		//게시물 단건 조회
		public BoardDTO readBoardById(int dealId) {			
			BoardVO boardVO = boardDAO.readBoardById(dealId);
			
			//BoardVO를 BoardDTO로 변환하여 반환
			return mapperUtil.map(boardVO, BoardDTO.class);
		}

		//게시물 목록
		public PageResponseDTO<BoardDTO> getBoardList(PageRequestDTO pageRequestDTO) {
			//DAO에서 게시물 목록을 가져오고, BoardVO를 BoardDTO로 변환
			List<BoardDTO> boardList = boardDAO.getBoardList(pageRequestDTO).stream().map(board -> mapperUtil.map(board, BoardDTO.class)).toList();
			//PageResponseDTO를 생성하여 반환
			return PageResponseDTO.<BoardDTO>withAll()
					.pageRequestDTO(pageRequestDTO)
					.dtoList(boardList)
					.total(boardDAO.getTotalCount(pageRequestDTO))
					.build();
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
