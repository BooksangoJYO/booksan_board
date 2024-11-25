package io.booksan.booksan_board.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardReservationDTO {

    private String bookTitle;
    private String bookImageUrl;
    private int dealId;
    private String title;
    private Date writeTime;
    private List<ImageFileDTO> imageFileDTOList;
}
