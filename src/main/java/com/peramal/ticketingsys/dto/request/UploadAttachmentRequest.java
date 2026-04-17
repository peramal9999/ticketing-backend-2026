package com.peramal.ticketingsys.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadAttachmentRequest {

    @Schema(type = "string", format = "binary", description = "File to upload")
    private MultipartFile file;
}
