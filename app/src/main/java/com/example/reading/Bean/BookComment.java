package com.example.reading.Bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookComment {
    private int cid;

    private int cuid;

    private int cbid;

    private String ccreateTime;

    private String content;

    private int loveCount;

    private String username;

    private String uimg;

    private int loveStatus;
}
