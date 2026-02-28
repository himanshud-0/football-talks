package com.footballtalks.footballtalks.service;

import com.footballtalks.footballtalks.dto.*;
import com.footballtalks.footballtalks.model.Post;
import com.footballtalks.footballtalks.model.PostLike;
import com.footballtalks.footballtalks.model.User;
import com.footballtalks.footballtalks.repository.CommentRepository;
import com.footballtalks.footballtalks.repository.PostLikeRepository;
import com.footballtalks.footballtalks.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthService authService;

    public List<PostResponse> findAll() {
        User currentUser = authService.getCurrentUserOptional().orElse(null);

        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> toResponse(post, currentUser))
                .toList();
    }

    public PostResponse findById(@NonNull Long id) {
        User currentUser = authService.getCurrentUserOptional().orElse(null);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        return toResponse(post, currentUser);
    }

    @Transactional
    public PostResponse create(@NonNull CreatePostRequest request) {

        User currentUser = authService.getCurrentUser();

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(currentUser);

        post = postRepository.save(post);

        return toResponse(post, currentUser);
    }

    @Transactional
    public PostResponse update(@NonNull Long id,
                               @NonNull UpdatePostRequest request) {

        User currentUser = authService.getCurrentUser();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to edit this post");
        }

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }

        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }

        post = postRepository.save(post);

        return toResponse(post, currentUser);
    }

    @Transactional
    public void delete(@NonNull Long id) {

        User currentUser = authService.getCurrentUser();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to delete this post");
        }

        postRepository.delete(post);
    }

    @Transactional
    public PostResponse toggleLike(@NonNull Long postId) {

        User currentUser = authService.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean alreadyLiked =
                likeRepository.existsByPostIdAndUserId(postId, currentUser.getId());

        if (alreadyLiked) {
            likeRepository.deleteByPostIdAndUserId(postId, currentUser.getId());
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        } else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(currentUser);
            likeRepository.save(like);
            post.setLikesCount(post.getLikesCount() + 1);
        }

        post = postRepository.save(post);

        return toResponse(post, currentUser);
    }

    private PostResponse toResponse(@NonNull Post post,
                                    @Nullable User currentUser) {

        PostResponse response = new PostResponse();

        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setLikesCount(post.getLikesCount());
        response.setCommentsCount(post.getCommentsCount());

        User author = post.getAuthor();

        UserSummaryDto authorDto = new UserSummaryDto(
                author.getId(),
                author.getUsername(),
                author.resolveFullName(),
                author.resolveProfileImageUrl(),
                author.resolveFavoriteTeam()
        );

        response.setAuthor(authorDto);

        boolean liked = currentUser != null
                && likeRepository.existsByPostIdAndUserId(post.getId(), currentUser.getId());

        response.setLikedByCurrentUser(liked);

        List<CommentResponse> comments =
                commentRepository.findByPostIdWithUser(post.getId())
                        .stream()
                        .map(this::toCommentResponse)
                        .toList();

        response.setComments(comments);

        return response;
    }

    private CommentResponse toCommentResponse(
            @NonNull com.footballtalks.footballtalks.model.Comment comment) {

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
