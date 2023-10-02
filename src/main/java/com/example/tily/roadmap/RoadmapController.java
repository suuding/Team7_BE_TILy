package com.example.tily.roadmap;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class RoadmapController {
    private final RoadmapService roadmapService;

    // 개인 로드맵(카테고리) 생성하기
    @PostMapping("/roadmaps/individual")
    public ResponseEntity<?> createIndividualRoadmap(@RequestBody @Valid RoadmapRequest.CreateIndividualRoadmapDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        RoadmapResponse.CreateIndividualRoadmapDTO responseDTO = roadmapService.createIndividualRoadmap(requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 그룹 로드맵 생성하기
    @PostMapping("/roadmaps")
    public ResponseEntity<?> createGroupRoadmap(@RequestBody @Valid RoadmapRequest.CreateGroupRoadmapDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails){
        RoadmapResponse.createGroupRoadmapDTO responseDTO = roadmapService.createGroupRoadmap(requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 틸리, 그룹 로드맵 정보 조회하기
    @GetMapping("/roadmaps/{id}")
    public ResponseEntity<?> findGroupRoadmap(){


        return ResponseEntity.ok().body(null);
    }
}