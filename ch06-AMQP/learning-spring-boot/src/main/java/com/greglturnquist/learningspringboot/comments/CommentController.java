package com.greglturnquist.learningspringboot.comments;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final RabbitTemplate rabbitTemplate;
    private final MeterRegistry meterRegistry;

    @PostMapping("/comments")
    public Mono<String> addComment(Mono<Comment> newComment) {
        return newComment.flatMap(comment ->
                Mono.fromRunnable(() -> rabbitTemplate
                        .convertAndSend(
                                "learning-spring-boot",
                                "comments.new",
                                comment))
                        .then(Mono.just(comment)))
                .log("commentService-publish")
                .flatMap(comment -> {
                    meterRegistry
                            .counter("comments.produced", "imageId", comment.getImageId())
                            .increment();
                    return Mono.just("redirect:/");
                });
    }
}
