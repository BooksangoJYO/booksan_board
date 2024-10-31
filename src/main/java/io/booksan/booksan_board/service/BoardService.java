package io.booksan.booksan_board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.booksan.booksan_board.dao.BoardDAO;
import io.booksan.booksan_board.dto.BoardDTO;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BoardVO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
		private final BoardDAO boardDAO;
		private final MapperUtil mapperUtil;
	
		public int insertBoard(BoardVO boardVO) {
			return boardDAO.insertBoard(boardVO);
		
	}

		public BoardVO readBoardById(int dealId) {
			
			return boardDAO.readBoardById(dealId);
		}

		public List<BoardDTO> getBoardList() {
			return boardDAO.getBoardList().stream().map(board -> mapperUtil.map(board, BoardDTO.class)).toList();
		}

		public int updateBoard(BoardVO boardVO) {
			
			return boardDAO.updateBoard(boardVO);
		}

}
