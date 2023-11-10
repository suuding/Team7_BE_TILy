package com.example.tily.til;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import com.example.tily.roadmap.RoadmapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TilController {

    private final TilService tilService;

    // til 생성하기
    @PostMapping("/tils")
    public ResponseEntity<?> createTil(@RequestBody @Valid TilRequest.CreateTilDTO requestDTO, Errors errors,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        TilResponse.CreateTilDTO responseDTO = tilService.createTil(requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, responseDTO));
    }

    // til 조회하기
    @GetMapping("/tils/{tilId}")
    public ResponseEntity<?> viewTil(@PathVariable Long tilId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {

        TilResponse.ViewDTO responseDTO = tilService.viewTil(tilId, userDetails.getUser());

        return ResponseEntity.ok(ApiUtils.success(HttpStatus.OK, responseDTO));
    }

    // til 수정하기 (저장하기)
    @PatchMapping("/tils/{tilId}")
    public ResponseEntity<?> updateTil(@PathVariable Long tilId,
                                       @RequestBody @Valid TilRequest.UpdateTilDTO requestDTO, Errors errors,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        tilService.updateTil(requestDTO, tilId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, null));
    }

    // til 제출하기
    @PostMapping("/tils/{tilId}")
    public ResponseEntity<?> submitTil(@PathVariable Long tilId,
                                       @RequestBody @Valid TilRequest.SubmitTilDTO requestDTO,  Errors errors,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        tilService.submitTil(requestDTO, tilId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, null));
    }

    // til 삭제하기
    @DeleteMapping("/tils/{tilId}")
    public ResponseEntity<?> deleteTil(@PathVariable Long tilId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        tilService.deleteTil(tilId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, null));
    }

    // 나의 til 목록 전체 조회하기
    @GetMapping("/tils/my")
    public ResponseEntity<?> findAllMyTil(@RequestParam(value = "roadmapId", required = false) Long roadmapId,
                                          @RequestParam(value = "date", required = false) String date,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "9") int size,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {

        TilResponse.FindAllDTO responseDTO = tilService.findAllMyTil(roadmapId, date, title, page, size, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, responseDTO));
    }

    //  로드맵의 특정 step의 til 목록 조회하기
    @GetMapping("/steps/{stepId}/tils")
    public ResponseEntity<?> findTilOfStep(@PathVariable Long stepId,
                                           @RequestParam(value="isSubmit", defaultValue = "true") boolean isSubmit,
                                           @RequestParam(value="isMember", defaultValue = "true") boolean isMember,
                                           @RequestParam(value="name", required = false) String name){
        RoadmapResponse.FindTilOfStepDTO responseDTO = tilService.findTilOfStep(stepId, isSubmit, isMember, name);

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, responseDTO));
    }
}
