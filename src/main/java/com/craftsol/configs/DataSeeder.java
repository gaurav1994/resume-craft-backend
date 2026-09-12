package com.craftsol.configs;

import com.craftsol.entity.*;
import com.craftsol.repository.ResumeRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements ApplicationListener<ApplicationReadyEvent> {

    private final ResumeRepository resumeRepository;

    public DataSeeder(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (resumeRepository.count() == 0) {
            ContactInformation contactInformation = ContactInformation.builder()
                .firstName("Gaurav")
                .lastName("Singh")
                .email("gkgarry911@gmail.com")
                .phone("8076926462")
                .website("")
                .address("Pune, Maharashtra")
                .build();

            Summary summary = Summary.builder()
                .headline("Senior Java Backend Engineer")
                .profSummary("Backend engineer building reliable financial services with Java, Spring Boot, microservices, AWS, and Kafka.")
                .build();

            Resume resume = Resume.builder()
                .title("Senior Java Backend Resume")
                .currentRole("Software Engineering")
                .updated("Updated today")
                .contactInformation(contactInformation)
                .summary(summary)
                .build();

            Experience experience1 = Experience.builder()
                .company("Capgemini India Pvt. Ltd")
                .designation("Java Backend Developer")
                .date("Jan 2025 to July 2026")
                .details("Designed scalable backend services using Java 17, Spring Boot, JPA, and REST APIs for enterprise financial applications.")
                .resume(resume)
                .build();

            Experience experience2 = Experience.builder()
                .company("Coforge Pvt. Ltd.")
                .designation("Senior Software Engineer")
                .date("June 2023 to Dec 2024")
                .details("Built event-driven services and improved API reliability across banking workflows.")
                .resume(resume)
                .build();

            Project project = Project.builder()
                .projectTitle("Banking Payments Platform")
                .url("https://example.com/payments")
                .date("Jan 2025 to July 2026")
                .details("Designed scalable backend services using Java 17, Spring Boot, JPA, and REST APIs.")
                .resume(resume)
                .build();

            Education education = Education.builder()
                .institute("University of Pune")
                .degree("Bachelor of Computer Applications")
                .date("2019 to 2022")
                .details("Focused on software engineering and distributed systems.")
                .resume(resume)
                .build();

            Skill java = Skill.builder().name("Java").resume(resume).build();
            Skill spring = Skill.builder().name("Spring Boot").resume(resume).build();
            Skill aws = Skill.builder().name("AWS").resume(resume).build();
            Skill kafka = Skill.builder().name("Kafka").resume(resume).build();
            Skill microservices = Skill.builder().name("Microservices").resume(resume).build();

            resume.setExperience(List.of(experience1, experience2));
            resume.setProjects(List.of(project));
            resume.setEducation(List.of(education));
            resume.setSkills(List.of(java, spring, aws, kafka, microservices));

            resumeRepository.save(resume);
        }
    }
}
