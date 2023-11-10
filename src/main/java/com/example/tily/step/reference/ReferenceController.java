package com.example.tily.step.reference;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import com.example.tily.step.StepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReferenceController {

    private final ReferenceService referenceService;

    // step의 참고자료 생성하기
    @PostMapping("/references")
    public ResponseEntity<?> createReference(@RequestBody @Valid ReferenceRequest.CreateReferenceDTO requestDTO, Errors errors,
                                             @AuthenticationPrincipal CustomUserDetails userDetails){
        referenceService.createReference(requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, null));
    }

    // step의 참고자료 목록 조회

    @GetMapping("/steps/{stepId}/references")
    public ResponseEntity<?> findReference(@PathVariable("stepId") Long stepId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails){
        StepResponse.FindReferenceDTO responseDTO = referenceService.findReference(stepId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, responseDTO));
    }

    // 참고자료 삭제
    @DeleteMapping("/references/{referenceId}")
    public ResponseEntity<?> deleteReference(@PathVariable("referenceId") Long referenceId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        referenceService.deleteReference(referenceId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, null));
    }
}
