package io.booksan.booksan_board.controller;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_board.dto.BoardDTO;
import io.booksan.booksan_board.dto.BookDTO;
import io.booksan.booksan_board.dto.BookInfoDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.dto.PageResponseDTO;
import io.booksan.booksan_board.dto.RequestDTO;
import io.booksan.booksan_board.service.BoardService;
import io.booksan.booksan_board.service.BookService;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BoardVO;
import io.booksan.booksan_board.vo.BookVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
	private final MapperUtil mapperUtil;
	private final BoardService boardService;
	private final BookService bookService;
	
	
	//게시물 등록	
	@PostMapping("/insert")
	public ResponseEntity<?> insertBoard(@RequestBody RequestDTO requestDTO, @AuthenticationPrincipal UserDetails userDetails ) {
		log.info("Request Data: {}", requestDTO);
		//게시물 등록 정보
		BoardDTO boardDTO = new BoardDTO();
		
		//email 정보 얻기
		String email = userDetails.getUsername();
		
		//Setter 메서드를 사용하여 필드값 설정(게시물 등록폼에서 얻어온 데이터 세팅)
		boardDTO.setTitle(requestDTO.getTitle());		
		boardDTO.setContent(requestDTO.getContent());
		boardDTO.setBooksCategoryId(requestDTO.getBooksCategoryId());
		boardDTO.setPrice(requestDTO.getPrice());
		boardDTO.setEmail(email);
		boardDTO.setIsbn(requestDTO.getIsbn());
		
		//책 정보 설정
		BookDTO bookDTO = new BookDTO();
		bookDTO.setBookTitle(requestDTO.getBookTitle());
		bookDTO.setBookWriter(requestDTO.getBookWriter());
		bookDTO.setBookPublisher(requestDTO.getBookPublisher());
		bookDTO.setIsbn(requestDTO.getIsbn());
		
		//ISBN 존재 여부 확인(게시물 등록시 책정보테이블에 없는 ISBN인경우 책등록 요청)
		if(bookService.isISBNExists(requestDTO.getIsbn())==0) {
			int bookResult=bookService.insertBookInfo(mapperUtil.map(bookDTO, BookVO.class));
		}
		
		//책정보 등록이 먼저 등록이 되어야 게시물 등록할때 책정보테이블의 isbn를 참조할수 있음
		int boardResult=boardService.insertBoard(mapperUtil.map(boardDTO, BoardVO.class));		
		
		//응답 데이터를 저장할 Map 생성
		Map<String, Object> response = new HashMap<>();
		
		//boardResult가 1일경우 게시물 등록성공, 1이 아닌경우 게시물 등록 실패
		if(boardResult==1) {
			response.put("status", "success");
			response.put("message", "게시물 등록 성공");
			return ResponseEntity.ok(response);
		}else {
			response.put("status", "fail");
			response.put("message", "게시물 등록 실패");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}		
	}
	
	//게시물 단건조회
	@GetMapping("/read/{dealId}") 
	public ResponseEntity<?> readBoard(@PathVariable("dealId") int dealId){
		
		
		//단건조회 결과 boardVO에 담음
		BoardDTO boardDTO = boardService.readBoardById(dealId);
		
		
		//응답 데이터 저장할 Map
		Map<String, Object> response = new HashMap<>();
		
		if(boardDTO != null) {
			BookInfoDTO bookInfo = bookService.searchBook(boardDTO.getIsbn());
			response.put("status", "success");
			response.put("data", boardDTO);
			response.put("bookData", bookInfo);
			return ResponseEntity.ok(response);
		} else {
			response.put("stauts", "fail");
			response.put("message", "게시물을 찾을 수 없습니다.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		
	}
	
	//게시판 목록
	@GetMapping("/list")
	public ResponseEntity<?> getBoardList(PageRequestDTO pageRequestDTO){		
		log.info(pageRequestDTO.toString());
		//게시물 목록 가져와서 boadList에 담기
		PageResponseDTO<BoardDTO> boardList = boardService.getBoardList(pageRequestDTO);
		
		
		//응답데이터 저장할 Map
		Map<String,Object> response = new HashMap<>();
		log.info( boardList.toString());
		//게시물이 있는 경우
		if(!boardList.getDtoList().isEmpty()) {
			response.put("status", "success");
			response.put("data", boardList);
			return ResponseEntity.ok(response);
		} else {
			//게시물이 없는 경우
			response.put("status", "fail");
			response.put("message", "게시물이 없습니다.");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		}
	}
	
	//게시판 수정
	@PutMapping("/update")
	public ResponseEntity<?> updateBoard(@RequestBody BoardDTO boardDTO, @AuthenticationPrincipal UserDetails userDetails) {
		log.info(userDetails.getUsername());
		String email = userDetails.getUsername();
		//응답 데이터를 저장할 response
		Map<String, Object> response = new HashMap<>();
		
		boardDTO.setEmail(email);
		
		log.info(boardDTO.getEmail().toString());
		
		//로그인한 유저가 글작성자인지 확인하고 맞으면 수정 요청
		if ( boardDTO.getEmail().equals(email)) {
			
			int result = boardService.updateBoard(mapperUtil.map(boardDTO, BoardVO.class));	
			
			if(result ==1) {
				response.put("status", "success");
				response.put("message", "게시물 수정 성공");
				return ResponseEntity.ok(response);
			}  
		}
		
		response.put("status", "fail");
		response.put("message", "게시물 수정 실패");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		
		
	}
	
	
	//게시물 삭제
	@DeleteMapping("/delete/{dealId}")
	public ResponseEntity<?> deleteBoard(@PathVariable("dealId") int dealId, @RequestBody BoardDTO boardDTO , @AuthenticationPrincipal UserDetails userDetails){
		String email = userDetails.getUsername();
		boardDTO.setEmail(email);
		//응답데이터 저장할 response
		Map<String,Object> response = new HashMap<>();
		
		//로그인한 유저가 글작성자인지 확인하고 맞으면 삭제 요청
		if(boardDTO.getEmail().equals(email)) {
			int result = boardService.deleteBoard(dealId);
			if(result ==1) {
				response.put("status", "success");
				response.put("message", "게시물 삭제 성공");
				return ResponseEntity.ok(response);
			} 
		}	
		
		response.put("status", "fail");
		response.put("message", "게시물 삭제 실패");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		
	} 


    
    @GetMapping("favorite/list")
    public ResponseEntity<?> getFavoriteList(PageRequestDTO pageRequestDTO, @AuthenticationPrincipal UserDetails userDetails) {
        pageRequestDTO.setEmail(userDetails.getUsername());
        //게시물 목록 가져와서 boadList에 담기
        PageResponseDTO<BoardDTO> boardList = boardService.getFavoriteList(pageRequestDTO);

        //응답데이터 저장할 Map
        Map<String, Object> response = new HashMap<>();
        log.info(boardList.toString());
        //게시물이 있는 경우
        if (!boardList.getDtoList().isEmpty()) {
            response.put("status", "success");
            response.put("data", boardList);
            return ResponseEntity.ok(response);
        } else {
            //북마크 내역이 없을경우
            response.put("status", "fail");
            response.put("message", "북마크 내역이 없습니다.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
    }

    @PostMapping("favorite/insert/{dealId}")
    public ResponseEntity<?> insertFavoriteList(@PathVariable("dealId") int dealId, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        int result = boardService.insertFavorite(dealId, email);
        Map<String, Object> response = new HashMap<>();
        if (result == 1) {
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            //실패시
            response.put("status", "fail");
            response.put("message", "서버오류입니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
