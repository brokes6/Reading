package com.example.reading.ToolClass;

import java.util.List;

public class BookType {
    private int code;
    private String msg;
    private List<data> data;

    public static class data {
        private List<newBook>newBook;
        private List<popularBooks>popularBooks;
    }
    public static class newBook {
        private int bid;
        private int aid;
        private String bname;
    }
    public static class popularBooks {

    }
}
