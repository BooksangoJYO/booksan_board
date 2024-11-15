package io.booksan.booksan_board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import io.booksan.booksan_board.vo.ImageFileVO;

@Mapper
public interface ImageFileDAO {
    // 이미지 등록
    int insertImageFile(ImageFileVO imageFileVO);
    
    // 이미지 리스트 불러오기
    List<ImageFileVO> getImageFileList(int imgId);

    // 이미지 불러오기
    ImageFileVO readImageFile(int imgId);
}
