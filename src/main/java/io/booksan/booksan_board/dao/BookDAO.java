package io.booksan.booksan_board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.booksan.booksan_board.dto.BookCommentDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.vo.BookCategoryVO;
import io.booksan.booksan_board.vo.BookCommentVO;
import io.booksan.booksan_board.vo.BookVO;
import io.booksan.booksan_board.vo.FavoriteBookVO;

@Mapper
public interface BookDAO {

    //책 카테고리 목록 가져오기
    List<BookCategoryVO> getCategories();

    //게시물 등록시 책정보 등록
    int insertBookInfo(BookVO bookInfoVO);

    //isbn이 존재하는지 여부(책정보 등록시 존재하면 미등록, 존재하지않으면 등록)
    int isISBNExists(String isbn);

    //isbn에 해당하는 책 평가 댓글 목록 가져오기
    List<BookCommentDTO> getComment(String isbn);

    //isbn에 해당하는 책 평가 등록
    int insertComment(BookCommentVO bookCommentVO);

    //책 평가 댓글 수정
    int updateComment(BookCommentVO bookCommentVO);

    //책 평가 댓글 삭제
    int deleteComment(int commentId);

    List<BookVO> getFavoriteBookList(PageRequestDTO pageRequestDTO);

    int getFavoriteBookCount(PageRequestDTO pageRequestDTO);

    int insertFavoriteBook(FavoriteBookVO favoriteBooKVO);

    public void updateBookReadCount(String isbn);
}
