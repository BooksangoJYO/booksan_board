package io.booksan.booksan_board.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCommentDTO {
	private int commentId;
    private String email;
    private String content;
    private Date insertDatetime;
    private Date updatedDatetime;
    private String disabled;
    private String isbn;
}
