package com.greglturnquist.learningspringboot.comments;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@EnableBinding(CustomProcessor.class)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository repository;
    private final MeterRegistry meterRegistry;

    @StreamListener
    @Output(CustomProcessor.OUTPUT)
    public Flux<Comment> save(@Input(CustomProcessor.INPUT) Flux<Comment> newComments) {
        return repository
                .saveAll(newComments)
                .map(comment -> {
                    meterRegistry
                            .counter("comments.consumed", "imageId", comment.getImageId())
                            .increment();
                    return comment;
                });
    }

    @Bean
    CommandLineRunner setUpComments(MongoOperations operations) {
        return args -> {
            operations.dropCollection(Comment.class);
        };
    }
}
