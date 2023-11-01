package com.example.tily.til;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class TilController {

    private final TilService tilService;

    @PostMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils")
    public ResponseEntity<?> createTil(@PathVariable("roadmapId") Long roadmapId,
                                       @PathVariable("stepId") Long stepId,
                                       @RequestBody @Valid TilRequest.CreateTilDTO requestDTO,  Errors errors,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        TilResponse.CreateTilDTO responseDTO = tilService.createTil(requestDTO, roadmapId, stepId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    @PatchMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}")
    public ResponseEntity<?> updateTil(@PathVariable("roadmapId") Long roadmapId,
                                       @PathVariable("stepId") Long stepId,
                                       @PathVariable("tilId") Long tilId,
                                       @RequestBody @Valid TilRequest.UpdateTilDTO requestDTO, Errors errors,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        tilService.updateTil(requestDTO, tilId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    @GetMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}")
    public ResponseEntity<?> viewTil(@PathVariable("roadmapId") Long roadmapId,
                                     @PathVariable("stepId")Long stepId,
                                     @PathVariable("tilId") Long tilId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {

        TilResponse.ViewDTO responseDTO = tilService.viewTil(roadmapId, stepId, tilId, userDetails.getUser());
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }

    @PostMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}")
    public ResponseEntity<?> submitTil(@PathVariable("roadmapId") Long roadmapId,
                                       @PathVariable("stepId")Long stepId,
                                       @PathVariable("tilId") Long tilId,
                                       @RequestBody @Valid TilRequest.SubmitTilDTO requestDTO,  Errors errors,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        tilService.submitTil(requestDTO, roadmapId, stepId, tilId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    @DeleteMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}")
    public ResponseEntity<?> deleteTil(@PathVariable("roadmapId") Long roadmapId,
                                       @PathVariable("stepId")Long stepId,
                                       @PathVariable("tilId") Long tilId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        tilService.deleteTil(tilId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 나의 틸 목록 전체 조회하기
    @GetMapping("/tils/my")
    public ResponseEntity<?> findAllMyTil(@RequestParam(value = "roadmapId", required = false) Long roadmapId,
                                          @RequestParam(value = "date", required = false) String date,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "9") int size,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {

        TilResponse.FindAllDTO responseDTO = tilService.findAllMyTil(roadmapId, date, title, page, size, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
}
