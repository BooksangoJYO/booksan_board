package io.booksan.booksan_board.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardVO {
	private int dealId;
	private String title;
	private String content;
	private String nickname;
	private int booksCategoryId;
	private String isbn;
	private int price;
	private Date insertDatetime;
	private Date updatedDatetime;
	private int viewCnt;
	private int favoriteCnt;
	private String status;
	private String disabled;
}
