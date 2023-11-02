package com.example.tily.image;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Image {
    @Id
    @GeneratedValue
    private Long id;
    private String originalImageName;
    private String storageImageName;
    private String imagePath;

    public Image(String originalImageName, String storageImageName, String imagePath) {
        this.originalImageName = originalImageName;
        this.storageImageName = storageImageName;
        this.imagePath = imagePath;
    }
}
