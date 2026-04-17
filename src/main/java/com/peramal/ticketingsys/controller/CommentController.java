package com.peramal.ticketingsys.controller;

import com.peramal.ticketingsys.dto.request.CreateCommentRequest;
import com.peramal.ticketingsys.dto.response.CommentResponse;
import com.peramal.ticketingsys.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/tickets/{ticketId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable UUID ticketId,
                                                      @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(ticketId, request));
    }

    @GetMapping("/api/tickets/{ticketId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(commentService.getByTicketId(ticketId));
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
