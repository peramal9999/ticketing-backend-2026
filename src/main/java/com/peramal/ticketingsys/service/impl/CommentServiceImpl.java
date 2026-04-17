package com.peramal.ticketingsys.service.impl;

import com.peramal.ticketingsys.dto.request.CreateCommentRequest;
import com.peramal.ticketingsys.dto.response.CommentResponse;
import com.peramal.ticketingsys.entity.Comment;
import com.peramal.ticketingsys.entity.Ticket;
import com.peramal.ticketingsys.entity.User;
import com.peramal.ticketingsys.entity.enums.Role;
import com.peramal.ticketingsys.exception.AccessDeniedException;
import com.peramal.ticketingsys.exception.ResourceNotFoundException;
import com.peramal.ticketingsys.repository.CommentRepository;
import com.peramal.ticketingsys.repository.TicketRepository;
import com.peramal.ticketingsys.service.CommentService;
import com.peramal.ticketingsys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;

    @Override
    public CommentResponse addComment(UUID ticketId, CreateCommentRequest req) {
        User currentUser = SecurityUtils.getCurrentUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));
        Comment comment = Comment.builder()
                .ticket(ticket)
                .user(currentUser)
                .message(req.getMessage())
                .isInternal(req.getIsInternal() != null ? req.getIsInternal() : false)
                .build();
        CommentResponse response = toResponse(commentRepository.save(comment));
        log.info("Comment added: ticketId={}, commentId={}, by={}, internal={}",
                ticketId, response.getId(), currentUser.getEmail(), comment.getIsInternal());
        return response;
    }

    @Override
    public List<CommentResponse> getByTicketId(UUID ticketId) {
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void delete(UUID commentId) {
        User currentUser = SecurityUtils.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
        if (currentUser.getRole() != Role.ADMIN && !comment.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized comment delete attempt: commentId={}, by={}", commentId, currentUser.getEmail());
            throw new AccessDeniedException("You can only delete your own comments");
        }
        commentRepository.deleteById(commentId);
        log.info("Comment deleted: commentId={}, by={}", commentId, currentUser.getEmail());
    }

    private CommentResponse toResponse(Comment c) {
        return CommentResponse.builder()
                .id(c.getId())
                .ticketId(c.getTicket().getId())
                .userId(c.getUser().getId())
                .userName(c.getUser().getFirstName() + " " + c.getUser().getLastName())
                .message(c.getMessage())
                .isInternal(c.getIsInternal())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
