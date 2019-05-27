package com.greglturnquist.learningspringboot.learningspringbootchat;

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
public class OutboundChatService implements WebSocketHandler {
    private Flux<String> flux;
    private FluxSink<String> chatMessageSink;

    public OutboundChatService() {
        this.flux = Flux.<String>create(
                emitter -> this.chatMessageSink = emitter,
                FluxSink.OverflowStrategy.IGNORE)
                .publish()
                .autoConnect();
    }

    @StreamListener(ChatServiceStreams.BROKER_TO_CLIENT)
    public void listen(String message) {
        if (chatMessageSink != null) {
            log.info("Publishing " + message + " to websocket...");
            chatMessageSink.next(message);
        }
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session
                .send(this.flux
                        .map(session::textMessage)
                        .log("outbound-wrap-as-websocket-message")
                        .log("outbound-publish-to-websocket"));
    }
}
