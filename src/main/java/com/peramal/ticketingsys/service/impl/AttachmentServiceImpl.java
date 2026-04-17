package com.peramal.ticketingsys.service.impl;

import com.peramal.ticketingsys.dto.response.AttachmentResponse;
import com.peramal.ticketingsys.entity.Attachment;
import com.peramal.ticketingsys.entity.Ticket;
import com.peramal.ticketingsys.entity.User;
import com.peramal.ticketingsys.entity.enums.Role;
import com.peramal.ticketingsys.exception.AccessDeniedException;
import com.peramal.ticketingsys.exception.ResourceNotFoundException;
import com.peramal.ticketingsys.repository.AttachmentRepository;
import com.peramal.ticketingsys.repository.TicketRepository;
import com.peramal.ticketingsys.service.AttachmentService;
import com.peramal.ticketingsys.service.StorageService;
import com.peramal.ticketingsys.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.peramal.ticketingsys.service.impl.StorageServiceImpl.TICKETS_DIR;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;
    private final StorageService storageService;



    @Override
    public AttachmentResponse upload(UUID ticketId, MultipartFile file) {
        User currentUser = SecurityUtils.getCurrentUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        // Keep original filename for display purposes only
        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file"
        );

        // Delegate actual file storage to StorageService (saves with UUID-based random name)
        String fileUrl = storageService.save(TICKETS_DIR, ticketId, file);

        Attachment attachment = Attachment.builder()
                .ticket(ticket)
                .fileName(originalFilename)
                .fileUrl(fileUrl)
                .fileType(file.getContentType())
                .fileSize((int) file.getSize())
                .uploadedBy(currentUser)
                .build();
        AttachmentResponse response = toResponse(attachmentRepository.save(attachment));
        log.info("Attachment uploaded: ticketId={}, attachmentId={}, fileName={}, size={}B, by={}",
                ticketId, response.getId(), originalFilename, file.getSize(), currentUser.getEmail());
        return response;
    }

    @Override
    public List<AttachmentResponse> getByTicketId(UUID ticketId) {
        return attachmentRepository.findByTicketId(ticketId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void delete(UUID attachmentId) {
        User currentUser = SecurityUtils.getCurrentUser();
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId));

        if (currentUser.getRole() != Role.ADMIN &&
                (attachment.getUploadedBy() == null ||
                        !attachment.getUploadedBy().getId().equals(currentUser.getId()))) {
            log.warn("Unauthorized attachment delete attempt: attachmentId={}, by={}", attachmentId, currentUser.getEmail());
            throw new AccessDeniedException("You can only delete your own attachments");
        }

        // Delegate physical file deletion to StorageService
        storageService.delete(attachment.getFileUrl());

        attachmentRepository.deleteById(attachmentId);
        log.info("Attachment deleted: attachmentId={}, by={}", attachmentId, currentUser.getEmail());
    }

    @Override
    public Resource download(UUID attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId));

        log.info("Attachment downloaded: attachmentId={}, fileName={}", attachmentId, attachment.getFileName());
        // Delegate file retrieval to StorageService
        return storageService.load(attachment.getFileUrl());
    }

    private AttachmentResponse toResponse(Attachment a) {
        return AttachmentResponse.builder()
                .id(a.getId())
                .ticketId(a.getTicket().getId())
                .commentId(a.getComment() != null ? a.getComment().getId() : null)
                .fileName(a.getFileName())
                .fileType(a.getFileType())
                .fileSize(a.getFileSize())
                .uploadedById(a.getUploadedBy() != null ? a.getUploadedBy().getId() : null)
                .uploadedByName(a.getUploadedBy() != null ?
                        a.getUploadedBy().getFirstName() + " " + a.getUploadedBy().getLastName() : null)
                .createdAt(a.getCreatedAt())
                .build();
    }
}
