package com.greglturnquist.learningspringboot.learningspringbootchat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.tuple.Tuple;
import org.springframework.web.server.WebSession;

@Configuration
@Slf4j
public class GatewayConfig {

    static class SaveSessionGatewayFilterFactory implements GatewayFilterFactory<Tuple> {

        @Override
        public GatewayFilter apply(Tuple config) {
            return (exchange, chain) -> exchange.getSession()
                    .map(webSession -> {
                        log.debug("Session id: " + webSession.getId());
                        webSession.getAttributes().forEach((key, value) -> log.debug(key + " => " + value));
                        return webSession;
                    })
                    .map(WebSession::save)
                    .then(chain.filter(exchange));
        }

        //@Bean
        SaveSessionGatewayFilterFactory saveSessionGatewayFilterFactory() {
            return new SaveSessionGatewayFilterFactory();
        }
    }
}
