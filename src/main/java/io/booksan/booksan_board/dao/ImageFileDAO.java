package io.booksan.booksan_board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.booksan.booksan_board.vo.ImageFileVO;

@Mapper
public interface ImageFileDAO {
    // 이미지 등록
    int insertImageFile(ImageFileVO imageFileVO);
    
    // 이미지 리스트 불러오기
    List<ImageFileVO> getImageFileList(int dealId);

    // 이미지 불러오기
    ImageFileVO readImageFile(int dealId);

    int deleteImageFiles(@Param("imgIds") List<Integer> imgIds, @Param("dealId") int dealId);
}
