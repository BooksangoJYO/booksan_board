package io.booksan.booksan_board.dao;

import org.apache.ibatis.annotations.Mapper;

import io.booksan.booksan_board.vo.BoardVO;

@Mapper
public interface BoardDAO {

	//게시물 등록
	int insertBoard(BoardVO boardVO);

	//게시물 단건조회
	BoardVO readBoardById(int dealId);
	
	
	
	

}
