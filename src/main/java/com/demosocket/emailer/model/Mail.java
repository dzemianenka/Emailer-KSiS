package com.demosocket.emailer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
public class Mail {

    String host;
    String port;
    String username;
    String password;
    String recipient;
    String subject;
    String content;
    MultipartFile attachment;
}
