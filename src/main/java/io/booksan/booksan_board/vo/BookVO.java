package io.booksan.booksan_board.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookVO {

    private String isbn;
    private String bookTitle;
    private String bookWriter;
    private String bookPublisher;
    private String bookImageUrl;

}
