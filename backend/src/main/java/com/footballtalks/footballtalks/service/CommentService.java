package com.footballtalks.footballtalks.service;

import com.footballtalks.footballtalks.dto.CommentResponse;
import com.footballtalks.footballtalks.dto.CreateCommentRequest;
import com.footballtalks.footballtalks.model.Comment;
import com.footballtalks.footballtalks.model.Post;
import com.footballtalks.footballtalks.model.User;
import com.footballtalks.footballtalks.repository.CommentRepository;
import com.footballtalks.footballtalks.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthService authService;

    @Transactional
    public CommentResponse create(@NonNull Long postId,
                                  @NonNull CreateCommentRequest request) {

        User currentUser = authService.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setUser(currentUser);

        comment = commentRepository.save(comment);

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return toResponse(comment);
    }

    public List<CommentResponse> findByPostId(@NonNull Long postId) {
        return commentRepository.findByPostIdWithUser(postId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void delete(@NonNull Long commentId) {

        User currentUser = authService.getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }

        Post post = comment.getPost();
        post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
        postRepository.save(post);

        commentRepository.delete(comment);
    }

    private CommentResponse toResponse(@NonNull Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getUser().resolveFullName(),
                comment.getUser().resolveProfileImageUrl(),
                comment.getCreatedAt()
        );
    }
}
