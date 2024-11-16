package io.booksan.booksan_board.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FavoriteBookVO {

    private String isbn;
    private String email;
}
