package io.booksan.booksan_board.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_board.dto.BookCategoryDTO;
import io.booksan.booksan_board.dto.BookCommentDTO;
import io.booksan.booksan_board.dto.BookDTO;
import io.booksan.booksan_board.dto.BookInfoDTO;
import io.booksan.booksan_board.dto.BookRecommendPriceDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.dto.PageResponseDTO;
import io.booksan.booksan_board.service.BookService;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.util.TokenChecker;
import io.booksan.booksan_board.vo.BookCommentVO;
import io.booksan.booksan_board.vo.BookMarkedBookVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
@Slf4j
public class BookController {

    private final TokenChecker tokenChecker;
    private final MapperUtil mapperUtil;
    private final BookService bookService;

    //게시물 등록 페이지에서 키워드로 책정보 검색(네이버 검색 API)
    @GetMapping("/searchAll/{searchTitle}/{page}/{size}")
    public ResponseEntity<?> searchBooks(
            @PathVariable("searchTitle") String searchTitle,
            @PathVariable("page") int page,
            @PathVariable("size") int size
    ) {
        //PageRequestDTO 객체 생성
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .keyword(searchTitle) //검색어를 keyword 필드에 설정
                .build();

        //응답반환
        return bookService.searchBooks(pageRequestDTO);
    }

    //게시물 조회페이지에서 isbn으로 책정보 단건조회(네이버 검색 api)
    @GetMapping("/search/{isbn}")
    public ResponseEntity<?> searchBook(@PathVariable("isbn") String isbn, @RequestHeader Map<String, String> request) {
        String token = request.get("accesstoken");
        Map<String, Object> loginData = null;
        Map<String, Object> response = new HashMap<>();
        if (token != null) {
            loginData = tokenChecker.tokenCheck(token);
            if (loginData != null && (Boolean) loginData.get("status")) {
                boolean isBookMarkedBook = bookService.bookMarkBookCheck(new BookMarkedBookVO(isbn, (String) loginData.get("email")));
                response.put("isBookMarkedBook", isBookMarkedBook);
            }
        } else {
            response.put("isBookMarkedBook", false);
        }
        BookInfoDTO bookInfo = bookService.searchBook(isbn);
        response.put("status", "success");
        response.put("bookInfo", bookInfo);

        return ResponseEntity.ok(response);

    }

    //카테고리 목록 가져오기
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        //책 카테고리 정보 가져와서 booksCategories에 담기
        List<BookCategoryDTO> booksCategories = bookService.getCategories();

        //응답데이터 저장할 Map
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "카테고리 불러오기 성공");
        response.put("booksCategories", booksCategories);

        return ResponseEntity.ok(response);
    }

    //책 평가 댓글 목록 가져오기
    @GetMapping("/comment/list/{isbn}")
    public ResponseEntity<?> getCommentList(@PathVariable("isbn") String isbn) {
        //응답 데이터 저장할 Map
        Map<String, Object> response = new HashMap<>();

        if (isbn != null) {
            //게시글이 존재할 경우 책 평가 댓글 목록 가져오기
            List<BookCommentDTO> bookCommentList = bookService.getCommentList(isbn);

            response.put("status", "success");
            response.put("bookCommentList", bookCommentList);
            return ResponseEntity.ok(response);
        } else {
            response.put("stauts", "fail");
            response.put("message", "책 리뷰 목록을 찾을수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //책 평가 댓글 등록
    @PostMapping("/comment/insert")
    public ResponseEntity<?> insertBookComment(@RequestBody BookCommentDTO bookCommentDTO, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        //응답 데이터 생성
        Map<String, Object> response = new HashMap<>();

        //로그인한 유저의 email 세팅해주기
        bookCommentDTO.setEmail(email);

        if (bookCommentDTO.getEmail().equals(email)) {
            //댓글 등록
            int result = bookService.insertBookComment(mapperUtil.map(bookCommentDTO, BookCommentVO.class));

            if (result == 1) {
                response.put("status", "success");
                response.put("message", "댓글 등록 성공");
                return ResponseEntity.ok(response);
            }
        }
        response.put("status", "fail");
        response.put("message", "댓글 등록 실패");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

    }

    //책 평가 댓글 수정
    @PutMapping("/comment/update")
    public ResponseEntity<?> updateBookComment(@RequestBody BookCommentDTO bookCommentDTO, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        //응답 데이터 생성
        Map<String, Object> response = new HashMap<>();

        if (bookCommentDTO.getEmail().equals(email)) {
            //댓글 수정
            int result = bookService.updateBookComment(mapperUtil.map(bookCommentDTO, BookCommentVO.class));

            if (result == 1) {
                response.put("status", "success");
                response.put("message", "책 평가 댓글 수정 성공");
                return ResponseEntity.ok(response);
            }
        }

        response.put("status", "fail");
        response.put("message", "책 평가 댓글 수정 실패");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

    }

    //책 평가 댓글 삭제
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<?> deleteBookComment(@PathVariable("commentId") int commentId, @RequestBody BookCommentDTO bookCommentDTO, @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        //응답 데이터
        Map<String, Object> response = new HashMap<>();

        if (bookCommentDTO.getEmail().equals(email)) {
            int result = bookService.deleteComment(commentId);

            if (result == 1) {
                response.put("status", "success");
                response.put("message", "댓글 삭제 성공");
                return ResponseEntity.ok(response);
            }
        }

        response.put("status", "fail");
        response.put("message", "댓글 삭제 실패");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }

    @GetMapping("bookMark/book/list")
    public ResponseEntity<?> getBookMarkBookList(PageRequestDTO pageRequestDTO, @AuthenticationPrincipal UserDetails userDetails) {
        pageRequestDTO.setEmail(userDetails.getUsername());
        //게시물 목록 가져와서 boadList에 담기
        PageResponseDTO<BookDTO> bookList = bookService.getBookMarkBookList(pageRequestDTO);

        //응답데이터 저장할 Map
        Map<String, Object> response = new HashMap<>();
        //게시물이 있는 경우
        if (!bookList.getDtoList().isEmpty()) {
            response.put("status", "success");
            response.put("data", bookList);
            return ResponseEntity.ok(response);
        } else {
            //북마크 내역이 없을경우
            response.put("status", "fail");
            response.put("message", "북마크 내역이 없습니다.");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("bookMark/book/insert/{isbn}")
    public ResponseEntity<?> insertBookMarkBookList(@PathVariable("isbn") String isbn, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        int result = bookService.insertBookMarkBook(isbn, email);
        Map<String, Object> response = new HashMap<>();
        if (result == 1 || result == 2) {
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            //실패시
            response.put("status", "fail");
            response.put("message", "서버오류입니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //가격 추천
    @PostMapping("/recommendPrice")
    public ResponseEntity<?> getRecommendPrice(@RequestBody BookRecommendPriceDTO bookRecommendPriceDTO) {
        //publishDate가 Date 타입인 경우 이를 String으로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String publishDate = dateFormat.format(bookRecommendPriceDTO.getPublishDate());

        String isbn = (String) bookRecommendPriceDTO.getIsbn();
        double originalPrice = Double.parseDouble(bookRecommendPriceDTO.getBookOriginalPrice());

        //출판 연도와 연 추출
        int publishYear = Integer.parseInt(publishDate.split("-")[0]);
        int publishMonth = Integer.parseInt(publishDate.split("-")[1]);

        //추천 가격 계산
        double recommendedPrice = bookService.getRecommendPrice(originalPrice, publishYear, publishMonth);

        recommendedPrice = Math.round(recommendedPrice);

        //응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("recommendedPrice", recommendedPrice);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommended")
    public ResponseEntity<?> getRecommendedBooks() {
        Map<String, Object> response = new HashMap<>();

        try {
            // bookreadcount > 0 인 책이 있는지 확인하고 있으면 상위 4권, 없으면 랜덤 4권
            List<BookDTO> recommendedBooks = bookService.getRecommendedBooks();

            response.put("status", "success");
            response.put("data", recommendedBooks);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "fail");
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
