package io.booksan.booksan_board.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardReservationEntity {

    private String email;
    private int dealId;

    public BoardReservationEntity(String email) {
        this.email = email;
    }
}
