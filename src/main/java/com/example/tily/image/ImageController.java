package com.example.tily.image;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImageController {
    final private ImageService imageService;

    // 폴더 별로 관리 하기 위해 나눔(user, roadmap, post)

    @GetMapping("/images/users/{userId}")
    public ResponseEntity<?> findUserImage(@PathVariable Long userId){
        ImageResponse.UserImageDTO responseDTO = imageService.findUserImage(userId);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }


    @PostMapping("/images/users/{userId}")
    public ResponseEntity<?> uploadUserImage(@PathVariable Long userId, @RequestParam("image") MultipartFile file,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println("postmapping, user images");
        log.info("user image, controller");
        imageService.updateUserImage(userId, file, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }


    @GetMapping("/images/roadmaps/{roadmapId}")
    public ResponseEntity<?> findRoadmapImage(@PathVariable Long roadmapId){
        ImageResponse.RoadmapImageDTO responseDTO = imageService.findRoadmapImage(roadmapId);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    @PostMapping("/images/roadmaps/{roadmapId}")
    public ResponseEntity<?> uploadRoadmapImage(@PathVariable Long roadmapId,
                                             @RequestParam("image") MultipartFile file) {

        imageService.updateRoadmapImage(roadmapId, file);

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }


    @PostMapping("/images/write")
    public ResponseEntity<?> postImage(@RequestParam("image") MultipartFile file){
        ImageResponse.PostImageDTO responseDTO = imageService.postImage(file);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
}
