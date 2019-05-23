package com.greglturnquist.learningspringboot.learningspringbootchat;

import lombok.Data;

@Data
public class Comment {
    private String id;
    private String imageId;
    private String comment;
}