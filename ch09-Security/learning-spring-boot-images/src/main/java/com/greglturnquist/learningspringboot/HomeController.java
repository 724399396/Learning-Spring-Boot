package com.greglturnquist.learningspringboot;

import com.greglturnquist.learningspringboot.image.CommentHelper;
import com.greglturnquist.learningspringboot.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{filename:.+}";

    private final ImageService imageService;
    private final CommentHelper commentHelper;

    @GetMapping(value = BASE_PATH + "/" + FILENAME + "/raw",
            produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<?>> onwRawImage(
            @PathVariable String filename) {
        return imageService.findOneImage(filename)
                .map(resource -> {
                    try {
                        return ResponseEntity.ok()
                                .contentLength(resource.contentLength())
                                .body(new InputStreamResource(resource.getInputStream()));
                    } catch (IOException e) {
                        return ResponseEntity.badRequest()
                                .body("Couldn't find " + filename + " => " + e.getMessage());
                    }
                });
    }

    @PostMapping(value = BASE_PATH)
    public Mono<String> createFile(
            @RequestPart(name = "file") Flux<FilePart> files,
            @AuthenticationPrincipal Principal principal) {
        return imageService.createImage(files, principal)
                .then(Mono.just("redirect:/"));
    }

    @DeleteMapping(BASE_PATH + "/" + FILENAME)
    public Mono<String> deleteFile(@PathVariable String filename) {
        return imageService.deleteImage(filename)
                .then(Mono.just("redirect:/"));
    }

    @GetMapping("/")
    public Mono<String> index(Model model) {
        model.addAttribute("images", imageService
                .findAllImages()
                .flatMap(image ->
                        Mono.just(image)
                                .zipWith(
                                        Mono.just(commentHelper.getComments(image)))
                                .map(imageAndComments -> new HashMap<String, Object>() {{
                                    put("id", imageAndComments.getT1().getId());
                                    put("name", imageAndComments.getT1().getName());
                                    put("owner", image.getOwner());
                                    put("comments", imageAndComments.getT2());
                                }})
                ));
        model.addAttribute("extra",
                "DevTools can also detect code changes too");
        return Mono.just("index");
    }
}
