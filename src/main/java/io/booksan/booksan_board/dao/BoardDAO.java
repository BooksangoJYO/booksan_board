package io.booksan.booksan_board.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import io.booksan.booksan_board.vo.BoardVO;

@Mapper
public interface BoardDAO {

	//게시물 등록
	int insertBoard(BoardVO boardVO);

	//게시물 단건조회
	BoardVO readBoardById(int dealId);

	//게시물 목록
	List<BoardVO> getBoardList();

	//게시물 수정
	int updateBoard(BoardVO boardVO);

	//게시물 삭제
	int deleteBoard(int dealId);
	
	
	
	

}
