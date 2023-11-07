package com.example.tily.til;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
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
    @GetMapping("/tils/{id}")
    public ResponseEntity<?> viewTil(@PathVariable Long id,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {

        TilResponse.ViewDTO responseDTO = tilService.viewTil(id, userDetails.getUser());
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }

    // til 수정하기 (저장하기)
    @PatchMapping("/tils/{id}")
    public ResponseEntity<?> updateTil(@PathVariable Long id,
                                       @RequestBody @Valid TilRequest.UpdateTilDTO requestDTO, Errors errors,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        tilService.updateTil(requestDTO, id, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // til 제출하기
    @PostMapping("/tils/{id}")
    public ResponseEntity<?> submitTil(@PathVariable Long id,
                                       @RequestBody @Valid TilRequest.SubmitTilDTO requestDTO,  Errors errors,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        tilService.submitTil(requestDTO, id, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // til 삭제하기
    @DeleteMapping("/tils/{id}")
    public ResponseEntity<?> deleteTil(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        tilService.deleteTil(id, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
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
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
}
