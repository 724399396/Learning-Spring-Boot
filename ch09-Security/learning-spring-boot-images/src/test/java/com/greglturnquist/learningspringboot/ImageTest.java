package com.greglturnquist.learningspringboot;

import com.greglturnquist.learningspringboot.image.Image;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageTest {
    @Test
    public void imagesManagedByLombokShouldWork() {
        Image image = new Image("id", "file-name.jpg", "admin");
        assertThat(image.getId()).isEqualTo("id");
        assertThat(image.getName()).isEqualTo("file-name.jpg");
    }
}
