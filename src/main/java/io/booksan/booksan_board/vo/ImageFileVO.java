package io.booksan.booksan_board.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageFileVO {
    private int imgId;
    private int dealId;
    private String imgUuid;
    private String imgName;
    private int imgSize;
    private String imgType;
    private Date uploadDate;
}
