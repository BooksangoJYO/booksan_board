package io.booksan.booksan_board.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookMarkCheckerVO {

    private int dealId;
    private String bookMarkEmail;

    public BookMarkCheckerVO(int dealId) {
        this.dealId = dealId;
    }
}
