package io.booksan.booksan_board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCategoryDTO {
	private int booksCategoryId;
	private String booksCategoryName;
}
