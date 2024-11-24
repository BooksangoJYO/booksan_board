package io.booksan.booksan_board.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCommentVO {
	private int commentId;
	private String isbn;
	private String email;
	private String content;
	private Date insertDatetime;
	private Date updatedDatetime;
	private String disabled;
}
