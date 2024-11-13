package io.booksan.booksan_board.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	//등록되어있는 책정보를 가져올때 사용하는 DTO
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