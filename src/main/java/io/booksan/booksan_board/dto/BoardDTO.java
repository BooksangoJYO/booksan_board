package io.booksan.booksan_board.dto;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private int dealId;
    private String title;
    private String content;
    private String email;
    private int booksCategoryId;
    private String isbn;
    private int price;
    private Date insertDatetime;
    private Date updatedDatetime;
    private int viewCnt;
    private String status;
    private String disabled;
    private String publishDate;
    private String isBookMarked;

    private List<MultipartFile> files;
    private List<ImageFileDTO> imageFileDTOList;
    private List<Integer> existingImageIds;
}
