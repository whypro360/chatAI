package com.main.chatai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.main.chatai.mapper")
@SpringBootApplication
public class ChatAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatAiApplication.class, args);
    }

}
