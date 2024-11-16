package io.booksan.booksan_board.service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
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
import io.booksan.booksan_board.dto.ImageFileDTO;
import io.booksan.booksan_board.dto.PageRequestDTO;
import io.booksan.booksan_board.dto.PageResponseDTO;
import io.booksan.booksan_board.dto.RequestDTO;
import io.booksan.booksan_board.util.MapperUtil;
import io.booksan.booksan_board.vo.BoardVO;
import io.booksan.booksan_board.vo.FavoriteVO;
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
//		private final String CLIENT_ID = "YOUR_CLIENT_ID";
//		private final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";
		
	//게시물 등록
	@Transactional
	public int insertBoard(RequestDTO requestDTO, @AuthenticationPrincipal UserDetails userDetails) {
		//email 정보 얻기
		String email = userDetails.getUsername();
		
		//게시물 등록폼에서 얻어온 데이터 세팅
		BoardVO boardVO = new BoardVO();
		boardVO.setTitle(requestDTO.getTitle());		
		boardVO.setContent(requestDTO.getContent());
		boardVO.setBooksCategoryId(requestDTO.getBooksCategoryId());
		boardVO.setPrice(requestDTO.getPrice());
		boardVO.setEmail(email);
		if (requestDTO.getIsbn() != null) {
			boardVO.setIsbn(requestDTO.getIsbn());	
		}
		
		// 이미지 등록
		final int result = boardDAO.insertBoard(boardVO);
		try {
			for (MultipartFile file : requestDTO.getFiles()) {
				if (!file.isEmpty()) {
					String imageUuid = UUID.randomUUID().toString();

					OutputStream os = new FileOutputStream("/Users/user/" + imageUuid);
					file.getInputStream().transferTo(os);
					os.close();

					ImageFileVO imageFileVO = new ImageFileVO();

					imageFileVO.setDealId(boardVO.getDealId());
					imageFileVO.setImgName(file.getOriginalFilename());
					imageFileVO.setImgUuid(imageUuid);
					imageFileVO.setImgType(file.getContentType());
					imageFileVO.setImgSize((int)file.getSize());

					log.info("*** imageFileVO :" + imageFileVO.toString());

					imageFileDAO.insertImageFile(imageFileVO);
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//게시물 단건 조회
	public BoardDTO readBoardById(int dealId) {			
		BoardVO boardVO = boardDAO.readBoardById(dealId);
		BoardDTO boardDTO = mapperUtil.map(boardVO, BoardDTO.class);
		if(boardDTO != null) {
			boardDTO.setImageFileDTOList(
				imageFileDAO.getImageFileList(dealId)
					.stream()
					.map(imageFileVO -> mapperUtil.map(imageFileVO, ImageFileDTO.class))
					.collect(Collectors.toList())
			);

		}
		
		return boardDTO;
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

	public ImageFileDTO readImageFile(int imgId) {
		ImageFileVO imageFile = imageFileDAO.readImageFile(imgId);
		return mapperUtil.map(imageFile, ImageFileDTO.class);
	}

    //게시물 삭제
    public int deleteBoard(int dealId) {
        return boardDAO.deleteBoard(dealId);
    }

    public PageResponseDTO<BoardDTO> getFavoriteList(PageRequestDTO pageRequestDTO) {
        //DAO에서 게시물 목록을 가져오고, BoardVO를 BoardDTO로 변환
        List<BoardDTO> favoriteList = boardDAO.getFavoriteList(pageRequestDTO).stream().map(board -> mapperUtil.map(board, BoardDTO.class)).toList();
        //PageResponseDTO를 생성하여 반환
        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(favoriteList)
                .total(boardDAO.getFavoriteCount(pageRequestDTO))
                .build();
    }

    public int insertFavorite(int dealId, String email) {
        return boardDAO.insertFavorite(new FavoriteVO(dealId, email));
    }

}
