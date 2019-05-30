package com.greglturnquist.learningspringboot.learningspringbootchat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public Mono<String> index(Model model, @AuthenticationPrincipal Authentication principal) {
        model.addAttribute("authentication", principal);
        return Mono.just("index");
    }
}
