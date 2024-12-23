package io.booksan.booksan_board.vo;

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
    private String email;
    private int booksCategoryId;
    private String isbn;
    private int price;
    private Date insertDatetime;
    private Date updatedDatetime;
    private int viewCnt;
    private String status;
    private String disabled;
    private String publishDate;
    private String isBookMarked;
}
