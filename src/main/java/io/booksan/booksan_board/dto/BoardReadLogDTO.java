package io.booksan.booksan_board.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardReadLogDTO {

    private int dealReadId;
    private String email;
    private String isbn;
    private int dealId;
    private Date readTime;
}
