package com.example.reading.Bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentVo {
    private List<PostComment> comments;
    private int count;
}
