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

    @Column
    private String originalImageName;

    @Column
    private String storageImageName;

    @Column
    private String imagePath;

    public Image(String originalImageName, String storageImageName, String imagePath) {
        this.originalImageName = originalImageName;
        this.storageImageName = storageImageName;
        this.imagePath = imagePath;
    }
}
