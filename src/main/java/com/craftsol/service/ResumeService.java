package com.craftsol.service;

import com.craftsol.dto.ResumeDto;

import java.util.List;

public interface ResumeService {
    List<ResumeDto> findAll();
    ResumeDto findById(Long id);
    ResumeDto create(ResumeDto dto);
    ResumeDto update(Long id, ResumeDto dto);
    void delete(Long id);
}
