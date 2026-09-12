package com.craftsol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeDto {
    private Long id;
    private String title;
    private String currentRole;
    private String updated;
    private ContactInformationDto contactInformation;
    private SummaryDto summary;
    private List<ExperienceDto> experience;
    private List<ProjectDto> projects;
    private List<EducationDto> education;
    private List<String> skills;
}
