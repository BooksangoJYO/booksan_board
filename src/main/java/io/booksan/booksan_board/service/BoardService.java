package io.booksan.booksan_board.service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import io.booksan.booksan_board.dao.BoardDAO;
import io.booksan.booksan_board.dao.ImageFileDAO;
import io.booksan.booksan_board.dto.BoardDTO;
import io.booksan.booksan_board.dto.BoardReservationDTO;
import io.booksan.booksan_board.dto.BookCategoryDTO;
import io.booksan.booksan_board.dto.BookDTO;
import io.booksan.booksan_board.dto.ImageFileDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.dto.PageResponseDTO;
import io.booksan.booksan_board.dto.RequestDTO;
import io.booksan.booksan_board.entity.BoardReservationEntity;
import io.booksan.booksan_board.entity.BookAlertEntity;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BoardReadLogVO;
import io.booksan.booksan_board.vo.BoardVO;
import io.booksan.booksan_board.vo.BookMarkCheckerVO;
import io.booksan.booksan_board.vo.ImageFileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardDAO boardDAO;
    private final MapperUtil mapperUtil;

    private final ImageFileDAO imageFileDAO;

    // 게시물 등록
    @Transactional
    public int insertBoard(RequestDTO requestDTO, @AuthenticationPrincipal UserDetails userDetails) {
        // email 정보 얻기
        String email = userDetails.getUsername();

        // 게시물 등록폼에서 얻어온 데이터 세팅
        BoardVO boardVO = new BoardVO();
        boardVO.setTitle(requestDTO.getTitle());
        boardVO.setContent(requestDTO.getContent());
        boardVO.setBooksCategoryId(requestDTO.getBooksCategoryId());
        boardVO.setPrice(requestDTO.getPrice());
        boardVO.setEmail(email);
        boardVO.setPublishDate(requestDTO.getPublishDate());
        if (requestDTO.getIsbn() != null) {
            boardVO.setIsbn(requestDTO.getIsbn());
        }

        // 이미지 등록
        final int result = boardDAO.insertBoard(boardVO);
        try {
            if (requestDTO.getFiles() != null) {
                for (MultipartFile file : requestDTO.getFiles()) {
                    if (!file.isEmpty()) {
                        String imageUuid = UUID.randomUUID().toString();

                        OutputStream os = new FileOutputStream("/Users/Public/download/" + imageUuid);
                        file.getInputStream().transferTo(os);
                        os.close();

                        ImageFileVO imageFileVO = new ImageFileVO();

                        imageFileVO.setDealId(boardVO.getDealId());
                        imageFileVO.setImgName(file.getOriginalFilename());
                        imageFileVO.setImgUuid(imageUuid);
                        imageFileVO.setImgType(file.getContentType());
                        imageFileVO.setImgSize((int) file.getSize());

                        log.info("*** imageFileVO :" + imageFileVO.toString());

                        imageFileDAO.insertImageFile(imageFileVO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("****책 등록 오류!!!!!!");
        // 해당 책에 대한 거래에 알림설정을 해논 사용자들에게 알림 설정
        List<String> reservationPeople = boardDAO.getReservationPeople(boardVO.getIsbn());
        if (reservationPeople != null && !reservationPeople.isEmpty()) {
            for (String userEmail : reservationPeople) {
                BoardReservationEntity boardReservationEntity = new BoardReservationEntity();
                boardReservationEntity.setEmail(userEmail);
                boardReservationEntity.setDealId(boardVO.getDealId());
                boardDAO.insertBoardReservation(boardReservationEntity);
                log.info("***카운트 오류!!!!!!");
                boardDAO.updateBookAlert(new BookAlertEntity(userEmail, "increase", 0));
            }
        }

        return result;
    }

    // 게시물 단건 조회
    public BoardDTO readBoardById(int dealId, Map<String, Object> loginData) {
        String email = null;
        if (loginData != null && (Boolean) loginData.get("status")) {
            email = (String) loginData.get("email");
        }
        BookMarkCheckerVO bookMarkCheckerVO = new BookMarkCheckerVO(dealId);
        if (email != null) {
            bookMarkCheckerVO.setBookMarkEmail(email);
        }
        BoardVO boardVO = boardDAO.readBoardById(bookMarkCheckerVO);
        log.info("단건 조회 데이터" + boardVO.toString());
        if (boardVO != null) {
            BoardDTO boardDTO = mapperUtil.map(boardVO, BoardDTO.class);
            if (boardDTO != null) {
                boardDTO.setImageFileDTOList(imageFileDAO.getImageFileList(dealId).stream()
                        .map(imageFileVO -> mapperUtil.map(imageFileVO, ImageFileDTO.class))
                        .collect(Collectors.toList()));

            }
            // db에 로그 저장
            BoardReadLogVO boardReadLog = new BoardReadLogVO();
            boardReadLog.setDealId(boardVO.getDealId());
            boardReadLog.setIsbn(boardVO.getIsbn());
            boardDAO.updateDealReadCount(boardVO.getDealId());
            if (email != null) {
                boardReadLog.setEmail((String) loginData.get("email"));
            }
            boardDAO.insertBoardReadLog(boardReadLog);
            return boardDTO;
        }
        return null;
    }

    // 게시물 목록
    public PageResponseDTO<BoardDTO> getBoardList(PageRequestDTO pageRequestDTO) {
        // DAO에서 게시물 목록을 가져오고, BoardVO를 BoardDTO로 변환
        List<BoardDTO> boardList = boardDAO.getBoardList(pageRequestDTO).stream()
                .map(board -> mapperUtil.map(board, BoardDTO.class)).toList();
        // PageResponseDTO를 생성하여 반환
        return PageResponseDTO.<BoardDTO>withAll().pageRequestDTO(pageRequestDTO).dtoList(boardList)
                .total(boardDAO.getTotalCount(pageRequestDTO)).build();
    }

    // 게시물 수정
    @Transactional
    public int updateBoard(BoardDTO boardDTO) {
        // 게시물 등록폼에서 얻어온 데이터 세팅
        BoardVO boardVO = new BoardVO();
        boardVO.setDealId(boardDTO.getDealId());
        boardVO.setTitle(boardDTO.getTitle());
        boardVO.setContent(boardDTO.getContent());
        boardVO.setBooksCategoryId(boardDTO.getBooksCategoryId());
        boardVO.setPrice(boardDTO.getPrice());
        boardVO.setStatus(boardDTO.getStatus());

        ImageFileVO imageFileVO = new ImageFileVO();
        imageFileVO.setImgIds(boardDTO.getExistingImageIds());

        final int result = boardDAO.updateBoard(boardVO);
        try {
            if (boardDTO.getFiles() != null) {
                if (imageFileVO.getImgIds() == null) {
                    imageFileVO.setImgIds(new ArrayList<>());
                }
                imageFileDAO.deleteImageFiles(imageFileVO.getImgIds(), boardDTO.getDealId());
                for (MultipartFile file : boardDTO.getFiles()) {
                    if (!file.isEmpty()) {
                        String imageUuid = UUID.randomUUID().toString();

                        OutputStream os = new FileOutputStream("/Users/Public/download/" + imageUuid);
                        file.getInputStream().transferTo(os);
                        os.close();

                        imageFileVO = new ImageFileVO();

                        imageFileVO.setDealId(boardVO.getDealId());
                        imageFileVO.setImgName(file.getOriginalFilename());
                        imageFileVO.setImgUuid(imageUuid);
                        imageFileVO.setImgType(file.getContentType());
                        imageFileVO.setImgSize((int) file.getSize());

                        log.info("*** imageFileVO :" + imageFileVO.toString());

                        imageFileDAO.insertImageFile(imageFileVO);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public ImageFileDTO readImageFile(int imgId) {
        ImageFileVO imageFile = imageFileDAO.readImageFile(imgId);
        return mapperUtil.map(imageFile, ImageFileDTO.class);
    }

    // 게시물 삭제
    public int deleteBoard(int dealId) {
        return boardDAO.deleteBoard(dealId);
    }

    public PageResponseDTO<BoardDTO> getBookMarkList(PageRequestDTO pageRequestDTO) {
        // DAO에서 게시물 목록을 가져오고, BoardVO를 BoardDTO로 변환
        List<BoardDTO> bookMarkList = boardDAO.getBookMarkList(pageRequestDTO).stream()
                .map(board -> mapperUtil.map(board, BoardDTO.class)).toList();
        // PageResponseDTO를 생성하여 반환
        return PageResponseDTO.<BoardDTO>withAll().pageRequestDTO(pageRequestDTO).dtoList(bookMarkList)
                .total(boardDAO.getBookMarkCount(pageRequestDTO)).build();
    }

    public int insertBookMark(int dealId, String email) {
        return boardDAO.insertBookMark(new BookMarkCheckerVO(dealId, email));
    }

    public List<BoardReservationDTO> getBoardReservationList(String email) {
        List<BoardReservationDTO> bookReservationList = boardDAO.getBoardReservationList(email).stream()
                .map(boardReservationVO -> mapperUtil.map(boardReservationVO, BoardReservationDTO.class))
                .collect(Collectors.toList());
        int result = boardDAO.updateBoardReservation(new BoardReservationEntity(email));
        boardDAO.updateBookAlert(new BookAlertEntity(email, "decrease", result));
        return bookReservationList;
    }

    // 가판대 수정페이지 판매여부 전환
    public int updateStatus(BoardVO boardVO) {

        return boardDAO.updateStatus(boardVO);
    }

    public List<String> recommendBooksForAllUsers() {
        // 인기 top3 카테고리 찾기
        List<BookCategoryDTO> top3Categories = boardDAO.getTop3Categories().stream()
                .map(category -> mapperUtil.map(category, BookCategoryDTO.class)).toList();
        
        // 카테고리 별 랜덤 도서 1개 (반복문) -> 각각 리스트애 매핑
        List<String> recommendedBooksIsbn = new ArrayList<>();
        Random random = new Random();
        for (BookCategoryDTO category : top3Categories) {
            
            List<BoardVO> dealsInCategory = boardDAO.getDealsInCategory(category.getBooksCategoryId());
            
            if (!dealsInCategory.isEmpty()) {
                int randomIndex = random.nextInt(dealsInCategory.size());
                BoardVO randomDeal = dealsInCategory.get(randomIndex); // 카테고리에 해당하는 deal 중 randomindex에 해당하는 도서 가져오기
                String randomDealIsbn = randomDeal.getIsbn();
                
                recommendedBooksIsbn.add(randomDealIsbn);
                
            }
        }
        // 리턴
        return recommendedBooksIsbn;
    }

    public List<BookDTO> recommendBooksForUser(String email) {
    	// 유저 선호 카테고리 1~2개 찾기
        List<BookCategoryDTO> categoriesByFavoriteDeals = boardDAO.getCategoriesCountByFavoriteDeals(email).stream().map(category -> mapperUtil.map(category, BookCategoryDTO.class)).toList();
        List<BookCategoryDTO> categoriesByFavoriteBooks = boardDAO.getCategoriesCountByFavoriteBooks(email).stream().map(category -> mapperUtil.map(category, BookCategoryDTO.class)).toList();
        List<BookCategoryDTO> categoriesByRead = boardDAO.getCategoriesCountByRead(email).stream().map(category -> mapperUtil.map(category, BookCategoryDTO.class)).toList();
        
        Map<Integer, Integer> categoryScores = new HashMap<>(); // 카테고리 ID -> 점수
        
        log.info("**********************************categoriesByFavoriteDeals"+categoriesByFavoriteDeals.toString());
        int weightFavoriteDeals = 2; // favoriteDeals 가중치
        for (BookCategoryDTO category : categoriesByFavoriteDeals) {
            int categoryId = category.getBooksCategoryId();
            int count = category.getCount(); // count 필드 값 가져오기
            int score = count * weightFavoriteDeals; // count에 가중치를 곱한 점수
            categoryScores.put(categoryId, categoryScores.getOrDefault(categoryId, 0) + score);
        }
        log.info("**********************************categoriesByFavoriteBooks"+categoriesByFavoriteBooks.toString());
        int weightFavoriteBooks = 4; // favoriteBooks 가중치
        for (BookCategoryDTO category : categoriesByFavoriteBooks) {
            int categoryId = category.getBooksCategoryId();
            int count = category.getCount();
            int score = count * weightFavoriteBooks;
            categoryScores.put(categoryId, categoryScores.getOrDefault(categoryId, 0) + score);
        }
        log.info("**********************************categoriesByRead"+categoriesByRead.toString());
        int weightRead = 1; // read 가중치
        for (BookCategoryDTO category : categoriesByRead) {
            int categoryId = category.getBooksCategoryId();
            int count = category.getCount();
            int score = count * weightRead;
            categoryScores.put(categoryId, categoryScores.getOrDefault(categoryId, 0) + score);
        }

        // 점수가 높은 순으로 정렬
        List<Map.Entry<Integer, Integer>> sortedCategoryScores = categoryScores.entrySet()
            .stream()
            .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // 점수 내림차순 정렬
            .toList();

    	// 상위 1~2개 카테고리 가져오기
        int topCategoriesCount = 1; // 상위 몇 개를 추천할지 설정
        List<Integer> topCategoryIds = sortedCategoryScores.stream()
                .limit(topCategoriesCount)
                .map(Map.Entry::getKey)
                .toList();

        // 상위 카테고리에서 랜덤 도서 추천
        Map<Integer, BookDTO> recommendedBooks = new HashMap<>();
        Random random = new Random();
        for (Integer categoryId : topCategoryIds) {
            List<BookDTO> booksInCategory = boardDAO.getBooksByCategoryId(categoryId)
                    .stream()
                    .map(book -> mapperUtil.map(book, BookDTO.class))
                    .toList();

            if (!booksInCategory.isEmpty()) {
                BookDTO randomBook = booksInCategory.get(random.nextInt(booksInCategory.size()));
                recommendedBooks.put(categoryId, randomBook);
            }
        }

        return recommendedBooks.values().stream().toList();
    }
}
