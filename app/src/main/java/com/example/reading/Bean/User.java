package com.example.reading.Bean;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 创建于2019/9/27 15:30🐎
 * 修改于2020/2/20 14:35🐎
 */
@Getter
@Setter
@ToString
public class User implements Serializable {
    public static final long serialVersionUID=4725L;
    private Integer uid;
    private String account;
    private String username;
    private String password;
    private String uimg;
    private String token;

}
