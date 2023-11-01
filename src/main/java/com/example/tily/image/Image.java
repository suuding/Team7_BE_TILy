package com.example.tily.image;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="image_tb")
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
