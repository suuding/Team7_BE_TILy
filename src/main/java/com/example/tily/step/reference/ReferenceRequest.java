package com.example.tily.step.reference;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ReferenceRequest {
  
    public record FindReferenceDTO(Long stepId){
    }

    public record CreateReferenceDTO(Long stepId,
                                     @NotBlank(message = "참고자료의 카테고리를 입력해주세요.") String category,
                                     @NotBlank(message = "링크를 입력해주세요.") String link) {
    }
}
