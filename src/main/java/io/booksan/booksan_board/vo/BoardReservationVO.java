package io.booksan.booksan_board.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardReservationVO {

    private String bookTitle;
    private String bookImageUrl;
    private int dealId;
    private int title;
    private Date writeTime;
}
