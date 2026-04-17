package com.peramal.ticketingsys.service;

import com.peramal.ticketingsys.dto.response.AttachmentResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AttachmentService {
    AttachmentResponse upload(UUID ticketId, MultipartFile file);
    List<AttachmentResponse> getByTicketId(UUID ticketId);
    void delete(UUID attachmentId);
    Resource download(UUID attachmentId);
}
