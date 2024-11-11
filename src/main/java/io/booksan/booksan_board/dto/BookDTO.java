package io.booksan.booksan_board.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	private int booksCategoryId;
	private String booksCategoryName;
	
	private String title;
    private String link;
    private String image;
    private String author;
    private String discount;
    private String publisher;
    private String pubdate;
    private String isbn;
    private String description;
    
    private int commentId;
    private String uid;
    private String content;
    private Date insertDatetime;
    private Date updatedDatetime;
    private String disabled;
    

}