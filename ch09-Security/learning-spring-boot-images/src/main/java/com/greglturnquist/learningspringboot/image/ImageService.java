package com.greglturnquist.learningspringboot.image;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private static String UPLOAD_ROOT = "upload_dir";
    private final ResourceLoader resourceLoader;
    private final ImageRepository imageRepository;

    public Flux<Image> findAllImages() {
        return imageRepository.findAll()
                .log("findAll");
    }

    public Mono<Resource> findOneImage(String filename) {
        return Mono.fromSupplier(() ->
                resourceLoader.getResource(
                        "file:" + UPLOAD_ROOT + "/" + filename))
                .log("findOneImage");
    }

    public Mono<Void> createImage(Flux<FilePart> files, Principal auth) {
        return files
                .log("createImage-files")
                .flatMap(file -> {
                    Mono<Image> saveDatabaseImage = imageRepository.save(
                            new Image(
                                    UUID.randomUUID().toString(),
                                    file.filename(), auth.getName()))
                            .log("createImage-save");
                    Mono<Void> copyFile = Mono.just(
                            Paths.get(UPLOAD_ROOT, file.filename())
                                    .toFile())
                            .log("createImage-picktarget")
                            .map(destFile -> {
                                try {
                                    destFile.createNewFile();
                                    return destFile;
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .log("createImage-newfile")
                            .flatMap(file::transferTo)
                            .log("createImage-copy");
                    return Mono.when(saveDatabaseImage, copyFile)
                            .log("createImage-when");
                })
                .log("createImage-flatMap")
                .then()
                .log("createImage-done");
    }

    @PreAuthorize("hasRole('ADMIN') or " +
    "@imageRepository.findByName(#filename).owner == authentication.name")
    public Mono<Void> deleteImage(String filename) {
        Mono<Void> deleteDatabaseImage = imageRepository
                .findByName(filename)
                .log("deleteImage-find")
                .flatMap(imageRepository::delete)
                .log("deleteImage-record");

        Mono<Object> deleteFile = Mono.fromRunnable(() -> {
                    try {
                        Files.deleteIfExists(
                                Paths.get(UPLOAD_ROOT, filename));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).log("deleteImage-file");
        return Mono.when(deleteDatabaseImage, deleteFile)
                .log("deleteImage-when")
                .then()
                .log("deleteImage-done");
    }

    @Bean
    CommandLineRunner setUp() {
        return (args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectories(Paths.get(UPLOAD_ROOT));

            FileCopyUtils.copy("Test file",
                    new FileWriter(UPLOAD_ROOT +
                            "/learning-spring-boot-cover.jpg"));

            FileCopyUtils.copy("Test file2",
                    new FileWriter(UPLOAD_ROOT +
                            "/learning-spring-boot-2nd-edition-cover.jpg"));
            FileCopyUtils.copy("Test file3",
                    new FileWriter(UPLOAD_ROOT + "/bazinga.png"));
        };
    }
}
