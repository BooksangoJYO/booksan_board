package io.booksan.booksan_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	private int booksCategoryId;
	private String booksCategoryName;
	
	private String isbn;
	private String bookTitle;
	private String bookWriter;
	private String bookPublisher;

}
