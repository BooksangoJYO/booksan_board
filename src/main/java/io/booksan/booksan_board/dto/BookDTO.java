package io.booksan.booksan_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    //등록되어있는 책정보를 가져올때 사용하는 DTO

    private String isbn;
    private String bookTitle;
    private String bookWriter;
    private String bookPublisher;
    private String bookImageUrl;
}
