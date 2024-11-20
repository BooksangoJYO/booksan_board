package io.booksan.booksan_board.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FavoriteVO {

    private int dealId;
    private String email;

    public FavoriteVO(int dealId) {
        this.dealId = dealId;
    }
}
