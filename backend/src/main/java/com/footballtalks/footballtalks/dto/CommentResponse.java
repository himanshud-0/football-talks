package com.footballtalks.footballtalks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String fullName;
    private String profileImageUrl;
    private LocalDateTime createdAt;
}
