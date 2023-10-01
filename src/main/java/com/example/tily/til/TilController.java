package com.example.tily.til;

import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class TilController {

    private final TilService tilService;

    @PostMapping("/roadmaps/individual/{roadmapId}/steps/{stepId}/tils")
    public ResponseEntity<?> createTil(@PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId") Long stepId) {
        TilResponse.CreateTilDTO responseDTO = tilService.createTil();
        ApiUtils.ApiResult<?> apiResult= ApiUtils.success(responseDTO);

        return ResponseEntity.ok(apiResult);
    }
}
