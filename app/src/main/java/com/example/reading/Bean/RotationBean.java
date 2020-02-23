package com.example.reading.Bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RotationBean {
    private String sid;
    private String description;
    private String url;
    private int type;
    private int jumpType;
}
