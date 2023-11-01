package com.example.tily.image;

import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController {
    final private ImageService imageService;

    @GetMapping("/user/{userId}/image")
    public ResponseEntity<?> findUserImage(@PathVariable Long userId){
        ImageResponse.UserImageDTO responseDTO = imageService.findUserImage(userId);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    @PostMapping("/user/{userId}/image")
    public ResponseEntity<?> uploadUserImage(@PathVariable Long userId,
                                             @RequestParam("image") MultipartFile file) {

        imageService.updateUserImage(userId, file);

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    @GetMapping("/roadmap/{roadmapId}/image")
    public ResponseEntity<?> findRoadmapImage(@PathVariable Long roadmapId){
        ImageResponse.RoadmapImageDTO responseDTO = imageService.findRoadmapImage(roadmapId);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    @PostMapping("/roadmap/{roadmapId}/image")
    public ResponseEntity<?> uploadRoadmapImage(@PathVariable Long roadmapId,
                                             @RequestParam("image") MultipartFile file) {

        imageService.updateRoadmapImage(roadmapId, file);

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }
}
