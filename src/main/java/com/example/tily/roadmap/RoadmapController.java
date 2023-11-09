package com.example.tily.roadmap;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoadmapController {
    private final RoadmapService roadmapService;

    // 로드맵 생성하기
    @PostMapping("/roadmaps")
    public ResponseEntity<?> createRoadmap(@RequestBody @Valid RoadmapRequest.CreateRoadmapDTO requestDTO, Errors errors,
                                                @AuthenticationPrincipal CustomUserDetails userDetails){
        RoadmapResponse.CreateRoadmapDTO responseDTO = roadmapService.createRoadmap(requestDTO, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 틸리 로드맵 생성하기 - 임시 api
    @PostMapping("/roadmaps/tily")
    public ResponseEntity<?> createTilyRoadmap(@RequestBody @Valid RoadmapRequest.CreateTilyRoadmapDTO requestDTO, Errors errors,
                                                @AuthenticationPrincipal CustomUserDetails userDetails){
        RoadmapResponse.CreateRoadmapDTO responseDTO = roadmapService.createTilyRoadmap(requestDTO, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 틸리, 그룹 로드맵 정보 조회하기
    @GetMapping("/roadmaps/{id}")
    public ResponseEntity<?> findGroupRoadmap(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = Optional.ofNullable(userDetails).map(CustomUserDetails::getUser).orElse(null);
        RoadmapResponse.FindGroupRoadmapDTO responseDTO = roadmapService.findGroupRoadmap(id, user);
        
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 그룹 로드맵 정보 수정하기
    @PatchMapping("/roadmaps/{id}")
    public ResponseEntity<?> updateGroupRoadmap(@RequestBody @Valid RoadmapRequest.UpdateGroupRoadmapDTO requestDTO, Errors errors,
                                                @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.updateGroupRoadmap(id, requestDTO, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 내가 속한 로드맵 전체 목록 조회하기
    @GetMapping("/roadmaps/my")
    public ResponseEntity<?> findAllMyRoadmaps(@AuthenticationPrincipal CustomUserDetails userDetails) {
        RoadmapResponse.FindAllMyRoadmapDTO responseDTO = roadmapService.findAllMyRoadmaps(userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 로드맵 조회하기
    @GetMapping("/roadmaps")
    public ResponseEntity<?> findRoadmapByQuery(@RequestParam(value="category", defaultValue = "tily") String category,
                                                @RequestParam(value="name", required = false) String name,
                                                @RequestParam(value="page", defaultValue = "0") int page,
                                                @RequestParam(value="size", defaultValue = "12") int size,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        RoadmapResponse.FindRoadmapByQueryDTO responseDTO  = roadmapService.findAll(category, name, page, size);
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 그룹 로드맵에 참여 신청하기
    @PostMapping("/roadmaps/groups/{groupId}/apply")
    public ResponseEntity<?> applyGroupRoadmap(@RequestBody @Valid RoadmapRequest.ApplyRoadmapDTO requestDTO, Errors errors,
                                          @PathVariable("groupId") Long groupId, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.applyGroupRoadmap(requestDTO, groupId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 틸리 로드맵에 참여 신청하기
    @PostMapping("/roadmaps/tily/{tilyId}/apply")
    public ResponseEntity<?> applyTilyRoadmap(@PathVariable("tilyId") Long tilyId, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.applyTilyRoadmap(tilyId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 참가 코드로 로드맵 참여하기
    @PostMapping("/roadmaps/groups/participate")
    public ResponseEntity<?> participateRoadmap(@RequestBody @Valid RoadmapRequest.ParticipateRoadmapDTO requestDTO, Errors errors,
                                                @AuthenticationPrincipal CustomUserDetails userDetails){
        RoadmapResponse.ParticipateRoadmapDTO responseDTO = roadmapService.participateRoadmap(requestDTO, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 로드맵의 구성원 전체 조회하기
    @GetMapping("/roadmaps/groups/{groupId}/members")
    public ResponseEntity<?> findRoadmapMembers(@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails userDetails){
        RoadmapResponse.FindRoadmapMembersDTO responseDTO = roadmapService.findRoadmapMembers(groupId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 로드맵의 구성원 역할 바꾸기
    @PatchMapping("/roadmaps/groups/{groupId}/members/{memberId}")
    public ResponseEntity<?> changeMemberRole(@RequestBody @Valid RoadmapRequest.ChangeMemberRoleDTO requestDTO, Errors errors,
                                              @PathVariable Long groupId, @PathVariable Long memberId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.changeMemberRole(requestDTO, groupId, memberId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 로드맵의 구성원 강퇴하기
    @DeleteMapping("/roadmaps/groups/{groupId}/members/{memberId}")
    public ResponseEntity<?> dismissMember(@PathVariable Long groupId, @PathVariable Long memberId, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.dismissMember(groupId, memberId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 로드맵에 신청한 사람들 목록 조회하기
    @GetMapping("/roadmaps/groups/{groupId}/members/apply")
    public ResponseEntity<?> findAppliedUsers(@PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails userDetails){
        RoadmapResponse.FindAppliedUsersDTO responseDTO = roadmapService.findAppliedUsers(groupId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 로드맵 참여 신청 승인
    @PostMapping("/roadmaps/groups/{groupId}/members/{memberId}/accept")
    public ResponseEntity<?> acceptApplication(@PathVariable Long groupId, @PathVariable Long memberId, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.acceptApplication(groupId, memberId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 로드맵 참여 신청 거절
    @DeleteMapping("/roadmaps/groups/{groupId}/members/{memberId}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable Long groupId, @PathVariable Long memberId, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.rejectApplication(groupId, memberId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 그룹 로드맵 삭제
    @DeleteMapping("/roadmaps/{id}")
    public ResponseEntity<?> deleteRoadmap(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.deleteRoadmap(id, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }
}