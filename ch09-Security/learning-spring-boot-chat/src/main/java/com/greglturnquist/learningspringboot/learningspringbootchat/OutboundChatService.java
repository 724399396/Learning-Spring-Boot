package com.greglturnquist.learningspringboot.learningspringbootchat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

@Service
@EnableBinding(ChatServiceStreams.class)
@Slf4j
public class OutboundChatService extends AuthorizedWebSocketHandler {
    private Flux<Message<String>> flux;
    private FluxSink<Message<String>> chatMessageSink;

    public OutboundChatService() {
        this.flux = Flux.<Message<String>>create(
                emitter -> this.chatMessageSink = emitter,
                FluxSink.OverflowStrategy.IGNORE)
                .publish()
                .autoConnect();
    }

    @StreamListener(ChatServiceStreams.BROKER_TO_CLIENT)
    public void listen(Message<String> message) {
        if (chatMessageSink != null) {
            log.info("Publishing " + message + " to websocket...");
            chatMessageSink.next(message);
        }
    }

    @Override
    protected Mono<Void> doHandle(WebSocketSession session, String user) {
        return session
                .send(this.flux
                        .filter(s -> validate(s, user))
                        .map(this::transform)
                        .map(session::textMessage)
                        .log(user + "-outbound-wrap-as-websocket-message")
                        .log(user + "-outbound-publish-to-websocket"));
    }

    private boolean validate(Message<String> message, String user) {
        if (message.getPayload().startsWith("@")) {
            String targetUser = message.getPayload()
                    .substring(1, message.getPayload().indexOf(" "));

            String sender = message.getHeaders()
                    .get(ChatServiceStreams.USER_HEADER, String.class);

            return user.equals(sender) || user.equals(targetUser);
        } else {
            return true;
        }
    }

    private String transform(Message<String> message) {
        String user = message.getHeaders()
                .get(ChatServiceStreams.USER_HEADER, String.class);
        if (message.getPayload().startsWith("@")) {
            return "(" + user + "): " + message.getPayload();
        } else {
            return "(" + user + ")(all):" + message.getPayload();
        }
    }
}
