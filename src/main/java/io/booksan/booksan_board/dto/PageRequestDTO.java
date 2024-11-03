package io.booksan.booksan_board.dto;

import lombok.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequestDTO {
    @Builder.Default
    @Min(1)
    @Positive
    private int page = 1;

    @Builder.Default
    @Min(10)
    @Max(100)
    @Positive
    private int size = 10;

    private String searchType;  // 제목+내용, 도서명, 작가, 출판사
    private String keyword;
    private String from;
    private String to;

    public int getSkip() {
        return (page - 1) * size;
    }

    public String getLink() {
        return getParam(this.page);
    }

    public String getParam(int page) {
        StringBuilder builder = new StringBuilder();
        builder.append("page=" + page);
        builder.append("&size=" + size);
        
        if (searchType != null && !searchType.isEmpty()) {
            builder.append("&searchType=" + searchType);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            try {
                builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        if (from != null && !from.isEmpty()) {
            builder.append("&from=" + from);
        }
        if (to != null && !to.isEmpty()) {
            builder.append("&to=" + to);
        }

        return builder.toString();
    }
}
