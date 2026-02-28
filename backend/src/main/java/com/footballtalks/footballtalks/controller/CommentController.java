package com.footballtalks.footballtalks.controller;

import com.footballtalks.footballtalks.dto.CommentResponse;
import com.footballtalks.footballtalks.dto.CreateCommentRequest;
import com.footballtalks.footballtalks.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.findByPostId(postId));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId,
                                                          @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.ok(commentService.create(postId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
