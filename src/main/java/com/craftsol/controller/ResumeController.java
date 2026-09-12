package com.craftsol.controller;

import com.craftsol.dto.ResumeDto;
import com.craftsol.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping
    public ResponseEntity<List<ResumeDto>> getAllResumes() {
        return ResponseEntity.ok(resumeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeDto> getResumeById(@PathVariable Long id) {
        return ResponseEntity.ok(resumeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ResumeDto> createResume(@Valid @RequestBody ResumeDto resumeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resumeService.create(resumeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeDto> updateResume(@PathVariable Long id, @Valid @RequestBody ResumeDto resumeDto) {
        return ResponseEntity.ok(resumeService.update(id, resumeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        resumeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateResumePdf(@PathVariable Long id,
                                                     @RequestParam(value = "template", defaultValue = "classic") String template) {
        byte[] pdf = resumeService.generatePdf(id, template);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
            .filename("resume-" + id + "-" + template + ".pdf")
            .build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
