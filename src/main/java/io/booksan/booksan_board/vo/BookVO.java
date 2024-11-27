package io.booksan.booksan_board.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookVO {

    private String isbn;
    private String bookTitle;
    private String bookWriter;
    private String bookPublisher;
    private String bookImageUrl;

}
