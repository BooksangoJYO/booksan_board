package io.booksan.booksan_board.dao;


import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import io.booksan.booksan_board.vo.BookCategoryVO;
import io.booksan.booksan_board.vo.BookInfoVO;

@Mapper
public interface BookDAO {

	//책 카테고리 목록 가져오기
	List<BookCategoryVO> getCategories();

	//게시물 등록시 책정보 등록
	int insertBookInfo(BookInfoVO bookInfoVO);

	int isISBNExists(String isbn);

	
}
