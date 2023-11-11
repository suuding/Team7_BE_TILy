package com.example.tily.image;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.Errors;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImageController {
    final private ImageService imageService;

    // 폴더 별로 관리 하기 위해 나눔(user, roadmap, post)
    @GetMapping("/images/users/{userId}")
    public ResponseEntity<?> findUserImage(@PathVariable Long userId){
        ImageResponse.UserImageDTO responseDTO = imageService.findUserImage(userId);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, responseDTO));
    }

    @PostMapping("/images/users/{userId}/s3")
    public ResponseEntity<?> uploadUserImage(@PathVariable Long userId, @RequestParam("image") MultipartFile file,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        imageService.updateUserImageS3(userId, file, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, null));
    }

    @PostMapping("/images/users/{userId}")
    public ResponseEntity<?> uploadUserImage(@RequestBody @Valid ImageRequest.UpdateUserImageDTO requestDTO, Errors errors,
                                             @PathVariable Long userId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        imageService.updateUserImage(userId, requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, null));
    }

    @GetMapping("/images/roadmaps/{roadmapId}")
    public ResponseEntity<?> findRoadmapImage(@PathVariable Long roadmapId){
        ImageResponse.RoadmapImageDTO responseDTO = imageService.findRoadmapImage(roadmapId);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, responseDTO));
    }

    @PostMapping("/images/roadmaps/{roadmapId}")
    public ResponseEntity<?> uploadRoadmapImage(@PathVariable Long roadmapId,
                                             @RequestParam("image") MultipartFile file) {

        imageService.updateRoadmapImage(roadmapId, file);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, null));
    }

    @PostMapping("/images/write")
    public ResponseEntity<?> postImage(@RequestParam("image") MultipartFile file){
        ImageResponse.PostImageDTO responseDTO = imageService.postImage(file);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, responseDTO));
    }
}
