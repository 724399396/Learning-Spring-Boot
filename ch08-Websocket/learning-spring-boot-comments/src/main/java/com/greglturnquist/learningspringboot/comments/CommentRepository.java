package com.greglturnquist.learningspringboot.comments;

import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepository
        extends Repository<Comment, String> {

    Flux<Comment> findByImageId(String imageId);

    Flux<Comment> saveAll(Flux<Comment> newComment);

    // Needed to support save()
    Mono<Comment> findById(String id);

    Mono<Comment> deleteAll();
}
