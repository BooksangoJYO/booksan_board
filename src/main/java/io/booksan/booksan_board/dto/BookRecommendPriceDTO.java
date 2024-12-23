package io.booksan.booksan_board.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRecommendPriceDTO {

    private String isbn;
    private Date publishDate;
    private String bookOriginalPrice;
}
