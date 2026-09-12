package com.craftsol.service;

import com.craftsol.dto.*;
import com.craftsol.entity.*;
import com.craftsol.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeServiceImpl(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeDto> findAll() {
        return resumeRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeDto findById(Long id) {
        return resumeRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new RuntimeException("Resume not found with id: " + id));
    }

    @Override
    @Transactional
    public ResumeDto create(ResumeDto dto) {
        Resume resume = toEntity(dto);
        Resume saved = resumeRepository.save(resume);
        return toDto(saved);
    }

    @Override
    @Transactional
    public ResumeDto update(Long id, ResumeDto dto) {
        Resume existing = resumeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resume not found with id: " + id));

        existing.setTitle(dto.getTitle());
        existing.setCurrentRole(dto.getCurrentRole());
        existing.setUpdated(dto.getUpdated());

        if (dto.getContactInformation() != null) {
            ContactInformation contact = existing.getContactInformation();
            if (contact == null) {
                contact = new ContactInformation();
                existing.setContactInformation(contact);
            }
            contact.setFirstName(dto.getContactInformation().getFirstName());
            contact.setLastName(dto.getContactInformation().getLastName());
            contact.setEmail(dto.getContactInformation().getEmail());
            contact.setPhone(dto.getContactInformation().getPhone());
            contact.setWebsite(dto.getContactInformation().getWebsite());
            contact.setAddress(dto.getContactInformation().getAddress());
        }

        if (dto.getSummary() != null) {
            Summary summary = existing.getSummary();
            if (summary == null) {
                summary = new Summary();
                existing.setSummary(summary);
            }
            summary.setHeadline(dto.getSummary().getHeadline());
            summary.setProfSummary(dto.getSummary().getProfSummary());
        }

        existing.getExperience().clear();
        if (dto.getExperience() != null) {
            for (ExperienceDto expDto : dto.getExperience()) {
                Experience exp = Experience.builder()
                    .company(expDto.getCompany())
                    .designation(expDto.getDesignation())
                    .date(expDto.getDate())
                    .details(expDto.getDetails())
                    .resume(existing)
                    .build();
                existing.getExperience().add(exp);
            }
        }

        existing.getProjects().clear();
        if (dto.getProjects() != null) {
            for (ProjectDto projectDto : dto.getProjects()) {
                Project project = Project.builder()
                    .projectTitle(projectDto.getProjectTitle())
                    .url(projectDto.getUrl())
                    .date(projectDto.getDate())
                    .details(projectDto.getDetails())
                    .resume(existing)
                    .build();
                existing.getProjects().add(project);
            }
        }

        existing.getEducation().clear();
        if (dto.getEducation() != null) {
            for (EducationDto educationDto : dto.getEducation()) {
                Education education = Education.builder()
                    .institute(educationDto.getInstitute())
                    .degree(educationDto.getDegree())
                    .date(educationDto.getDate())
                    .details(educationDto.getDetails())
                    .resume(existing)
                    .build();
                existing.getEducation().add(education);
            }
        }

        existing.getSkills().clear();
        if (dto.getSkills() != null) {
            for (String skillName : dto.getSkills()) {
                Skill skill = Skill.builder()
                    .name(skillName)
                    .resume(existing)
                    .build();
                existing.getSkills().add(skill);
            }
        }

        return toDto(resumeRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Resume resume = resumeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resume not found with id: " + id));
        resumeRepository.delete(resume);
    }

    private Resume toEntity(ResumeDto dto) {
        Resume resume = Resume.builder()
            .title(dto.getTitle())
            .currentRole(dto.getCurrentRole())
            .updated(dto.getUpdated())
            .build();

        if (dto.getContactInformation() != null) {
            resume.setContactInformation(ContactInformation.builder()
                .firstName(dto.getContactInformation().getFirstName())
                .lastName(dto.getContactInformation().getLastName())
                .email(dto.getContactInformation().getEmail())
                .phone(dto.getContactInformation().getPhone())
                .website(dto.getContactInformation().getWebsite())
                .address(dto.getContactInformation().getAddress())
                .build());
        }

        if (dto.getSummary() != null) {
            resume.setSummary(Summary.builder()
                .headline(dto.getSummary().getHeadline())
                .profSummary(dto.getSummary().getProfSummary())
                .build());
        }

        resume.setExperience(new ArrayList<>());
        if (dto.getExperience() != null) {
            for (ExperienceDto expDto : dto.getExperience()) {
                Experience exp = Experience.builder()
                    .company(expDto.getCompany())
                    .designation(expDto.getDesignation())
                    .date(expDto.getDate())
                    .details(expDto.getDetails())
                    .resume(resume)
                    .build();
                resume.getExperience().add(exp);
            }
        }

        resume.setProjects(new ArrayList<>());
        if (dto.getProjects() != null) {
            for (ProjectDto projectDto : dto.getProjects()) {
                Project project = Project.builder()
                    .projectTitle(projectDto.getProjectTitle())
                    .url(projectDto.getUrl())
                    .date(projectDto.getDate())
                    .details(projectDto.getDetails())
                    .resume(resume)
                    .build();
                resume.getProjects().add(project);
            }
        }

        resume.setEducation(new ArrayList<>());
        if (dto.getEducation() != null) {
            for (EducationDto educationDto : dto.getEducation()) {
                Education education = Education.builder()
                    .institute(educationDto.getInstitute())
                    .degree(educationDto.getDegree())
                    .date(educationDto.getDate())
                    .details(educationDto.getDetails())
                    .resume(resume)
                    .build();
                resume.getEducation().add(education);
            }
        }

        resume.setSkills(new ArrayList<>());
        if (dto.getSkills() != null) {
            for (String skillName : dto.getSkills()) {
                Skill skill = Skill.builder()
                    .name(skillName)
                    .resume(resume)
                    .build();
                resume.getSkills().add(skill);
            }
        }

        return resume;
    }

    private ResumeDto toDto(Resume resume) {
        ResumeDto dto = ResumeDto.builder()
            .id(resume.getId())
            .title(resume.getTitle())
            .currentRole(resume.getCurrentRole())
            .updated(resume.getUpdated())
            .build();

        if (resume.getContactInformation() != null) {
            dto.setContactInformation(ContactInformationDto.builder()
                .firstName(resume.getContactInformation().getFirstName())
                .lastName(resume.getContactInformation().getLastName())
                .email(resume.getContactInformation().getEmail())
                .phone(resume.getContactInformation().getPhone())
                .website(resume.getContactInformation().getWebsite())
                .address(resume.getContactInformation().getAddress())
                .build());
        }

        if (resume.getSummary() != null) {
            dto.setSummary(SummaryDto.builder()
                .headline(resume.getSummary().getHeadline())
                .profSummary(resume.getSummary().getProfSummary())
                .build());
        }

        if (resume.getExperience() != null) {
            dto.setExperience(resume.getExperience().stream()
                .map(exp -> ExperienceDto.builder()
                    .id(exp.getId())
                    .company(exp.getCompany())
                    .designation(exp.getDesignation())
                    .date(exp.getDate())
                    .details(exp.getDetails())
                    .build())
                .collect(Collectors.toList()));
        }

        if (resume.getProjects() != null) {
            dto.setProjects(resume.getProjects().stream()
                .map(project -> ProjectDto.builder()
                    .id(project.getId())
                    .projectTitle(project.getProjectTitle())
                    .url(project.getUrl())
                    .date(project.getDate())
                    .details(project.getDetails())
                    .build())
                .collect(Collectors.toList()));
        }

        if (resume.getEducation() != null) {
            dto.setEducation(resume.getEducation().stream()
                .map(education -> EducationDto.builder()
                    .id(education.getId())
                    .institute(education.getInstitute())
                    .degree(education.getDegree())
                    .date(education.getDate())
                    .details(education.getDetails())
                    .build())
                .collect(Collectors.toList()));
        }

        if (resume.getSkills() != null) {
            dto.setSkills(resume.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toList()));
        }

        return dto;
    }
}
