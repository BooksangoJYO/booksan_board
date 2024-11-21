package io.booksan.booksan_board.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    //게시물 등록 정보

    private String title;
    private String content;
    private int booksCategoryId;
    private int price;
    private String email;
    private String publishDate;
    private List<MultipartFile> files;

    //책 정보
    private String isbn;
    private String bookTitle;
    private String bookWriter;
    private String bookPublisher;
    private String bookImageUrl;
}
