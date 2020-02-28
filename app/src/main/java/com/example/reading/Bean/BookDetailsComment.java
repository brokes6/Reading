package com.example.reading.Bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDetailsComment {
    private List<BookComment> comments;
    private int count;
}
