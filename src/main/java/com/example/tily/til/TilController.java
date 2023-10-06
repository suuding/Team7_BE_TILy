package com.example.tily.til;

import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class TilController {

    private final TilService tilService;

    @PostMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils")
    public ResponseEntity<?> createTil(@PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId") Long stepId, @RequestBody @Valid TilRequest.CreateTilDTO requestDTO) {
        TilResponse.CreateTilDTO responseDTO = tilService.createTil(requestDTO);
        ApiUtils.ApiResult<?> apiResult= ApiUtils.success(responseDTO);

        return ResponseEntity.ok(apiResult);
    }

    @PatchMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}")
    public ResponseEntity<?> updateTil(@PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId") Long stepId, @PathVariable("tilId") Long tilId, @RequestBody @Valid TilRequest.UpdateTilDTO requestDTO) {
        tilService.updateTil(requestDTO, tilId);
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    @GetMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}")
    public ResponseEntity<?> viewTil(@PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId")Long stepId, @PathVariable("tilId") Long tilId) {
        TilResponse.ViewDTO responseDTO = tilService.viewTil(tilId, stepId);
        ApiUtils.ApiResult<?> apiResult= ApiUtils.success(responseDTO);

        return ResponseEntity.ok(apiResult);
    }

    @PostMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}")
    public ResponseEntity<?> submitTil(@PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId")Long stepId, @PathVariable("tilId") Long tilId, @RequestBody @Valid TilRequest.SubmitTilDTO requestDTO) {

        tilService.submitTil(requestDTO, tilId);
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    @DeleteMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}")
    public ResponseEntity<?> deleteTil(@PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId")Long stepId, @PathVariable("tilId") Long tilId) {

        tilService.deleteTil(tilId);
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }
}