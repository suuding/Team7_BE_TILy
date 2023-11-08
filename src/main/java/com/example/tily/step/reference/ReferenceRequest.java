package com.example.tily.step.reference;

import java.time.LocalDateTime;

public class ReferenceRequest {
    public record FindReferenceDTO(Long stepId){
    }

    public record CreateReferenceDTO(String category, String link) {
    }
}
