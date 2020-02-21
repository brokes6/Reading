package com.example.reading.Bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCommentVo {
    private List<BookComment> commentList;

    private int count;
}
