package io.booksan.booksan_board.vo;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardReadLogVO {

    private int dealReadId;
    private String email;
    private String isbn;
    private int dealId;
    private Date readTime;
}
