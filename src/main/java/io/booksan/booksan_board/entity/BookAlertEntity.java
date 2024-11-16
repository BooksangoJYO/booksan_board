package io.booksan.booksan_board.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookAlertEntity {

    private String email;
    private String action;
    private int bookAlert;
}
