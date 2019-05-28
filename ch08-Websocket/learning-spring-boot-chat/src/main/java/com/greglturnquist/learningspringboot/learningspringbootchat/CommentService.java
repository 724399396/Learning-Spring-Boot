package com.greglturnquist.learningspringboot.learningspringbootchat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

@Service
@EnableBinding(ChatServiceStreams.class)
@Slf4j
public class CommentService implements WebSocketHandler {

    private ObjectMapper mapper;
    private Flux<Comment> flux;
    private FluxSink<Comment> webSocketCommentSink;

    public CommentService(ObjectMapper mapper) {
        this.mapper = mapper;
        this.flux = Flux.<Comment>create(
                emitter -> this.webSocketCommentSink = emitter,
                FluxSink.OverflowStrategy.IGNORE)
                .publish()
                .autoConnect();
    }

    @StreamListener(ChatServiceStreams.NEW_COMMENTS)
    public void broadcast(Comment comment) {
        if (webSocketCommentSink != null) {
            log.info("Publishing " + comment.toString() + " to websocket...");
            webSocketCommentSink.next(comment);
        }
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(this.flux
                .map(comment -> {
                    try {
                        return mapper.writeValueAsString(comment);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .log("encode-as-json")
                .map(session::textMessage)
                .log("wrap-as-websocket-message"))
                .log("publish-to-websocket");
    }
}
