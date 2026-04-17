package com.peramal.ticketingsys.service;

import com.peramal.ticketingsys.dto.request.CreateCommentRequest;
import com.peramal.ticketingsys.dto.response.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentResponse addComment(UUID ticketId, CreateCommentRequest request);
    List<CommentResponse> getByTicketId(UUID ticketId);
    void delete(UUID commentId);
}
