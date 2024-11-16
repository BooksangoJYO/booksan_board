package io.booksan.booksan_board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.entity.BoardReservationEntity;
import io.booksan.booksan_board.entity.BookAlertEntity;
import io.booksan.booksan_board.vo.BoardReservationVO;
import io.booksan.booksan_board.vo.BoardVO;
import io.booksan.booksan_board.vo.FavoriteVO;

@Mapper
public interface BoardDAO {

    //게시물 등록
    int insertBoard(BoardVO boardVO);

    //게시물 단건조회
    BoardVO readBoardById(int dealId);

    //게시물 목록
    List<BoardVO> getBoardList(PageRequestDTO pageRequestDTO);

    //게시물 수정
    int updateBoard(BoardVO boardVO);

    //게시물 삭제
    int deleteBoard(int dealId);

    //게시물 개수
    int getTotalCount(PageRequestDTO pageRequestDTO);

    List<BoardVO> getFavoriteList(PageRequestDTO pageRequestDTO);

    int getFavoriteCount(PageRequestDTO pageRequestDTO);

    int insertFavorite(FavoriteVO favoriteVO);
    
    //가판대 수정페이지 판매여부 변경
	int updateStatus(BoardVO boardVO);

    public List<BoardReservationVO> getBoardReservationList(String email);

    int insertBoardReservation(BoardReservationEntity boardReservationEntity);

    int updateBoardReservation(BoardReservationEntity boardReservationEntity);

    int updateBookAlert(BookAlertEntity bookAlertEntity);

    public List<String> getReservationPeople(String isbn);

}
