package com.greglturnquist.learningspringboot.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository repository;

    @GetMapping("/comments/{imageId}")
    public Flux<Comment> comments(@PathVariable String imageId) {
        return repository.findByImageId(imageId);
    }
}
