package com.greglturnquist.learningspringboot.learningspringbootchat;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Service
@EnableBinding(ChatServiceStreams.class)
@RequiredArgsConstructor
public class InboundChatService extends AuthorizedWebSocketHandler {
    private final ChatServiceStreams chatServiceStreams;


    @Override
    protected Mono<Void> doHandle(WebSocketSession session, String name) {
        return session
                .receive()
                .log(name + "-inbound-incoming-chat-message")
                .map(WebSocketMessage::getPayloadAsText)
                .log(name + "inbound-convert-to-text")
                .flatMap(message -> broadcast(message, name))
                .log(name + "-inbound-broadcast-to-broker")
                .then();
    }

    public Mono<?> broadcast(String message, String user) {
        return Mono.fromRunnable(() ->
                chatServiceStreams.clientToBroker().send(
                        MessageBuilder
                                .withPayload(message)
                                .setHeader(ChatServiceStreams.USER_HEADER, user)
                                .build()));
    }
}
