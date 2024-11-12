package io.booksan.booksan_board.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	private String bookTitle;
    private String link;
    private String image;
    private String bookWriter;
    private String discount;
    private String bookPublisher;
    private String pubdate;
    private String isbn;
    private String description;
    

    

}