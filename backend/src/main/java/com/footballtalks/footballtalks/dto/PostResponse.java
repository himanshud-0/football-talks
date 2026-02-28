package com.footballtalks.footballtalks.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer likesCount;
    private Integer commentsCount;

    private Boolean likedByCurrentUser;

    private UserSummaryDto author;

    private List<CommentResponse> comments;
}
