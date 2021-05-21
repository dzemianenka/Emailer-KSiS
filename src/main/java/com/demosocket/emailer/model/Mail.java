package com.demosocket.emailer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
public class Mail {

    String recipient;
    String subject;
    String content;
    MultipartFile attachment;
}
