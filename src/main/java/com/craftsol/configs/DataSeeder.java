package com.craftsol.configs;

import com.craftsol.entity.ContactInformation;
import com.craftsol.entity.Education;
import com.craftsol.entity.Experience;
import com.craftsol.entity.Project;
import com.craftsol.entity.Resume;
import com.craftsol.entity.Skill;
import com.craftsol.entity.Summary;
import com.craftsol.repository.ResumeRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements ApplicationListener<ApplicationReadyEvent> {

    private final ResumeRepository resumeRepository;

    public DataSeeder(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (resumeRepository.count() > 0) {
            return;
        }

        List<Resume> resumes = new ArrayList<>();

        resumes.add(createResume(
            "Gaurav",
            "Singh",
            "gkgarry911@gmail.com",
            "8076926462",
            "https://example.com",
            "Pune, Maharashtra",
            "Senior Java Backend Resume",
            "Software Engineering",
            "Senior Java Backend Engineer",
            "Backend engineer building reliable financial services with Java, Spring Boot, microservices, AWS, and Kafka.",
            "Capgemini India Pvt. Ltd",
            "Java Backend Developer",
            "Jan 2025 to July 2026",
            "Designed scalable backend services using Java 17, Spring Boot, JPA, and REST APIs for enterprise financial applications.",
            "Coforge Pvt. Ltd.",
            "Senior Software Engineer",
            "June 2023 to Dec 2024",
            "Built event-driven services and improved API reliability across banking workflows.",
            "Banking Payments Platform",
            "https://example.com/payments",
            "Jan 2025 to July 2026",
            "Designed scalable backend services using Java 17, Spring Boot, JPA, and REST APIs.",
            "University of Pune",
            "Bachelor of Computer Applications",
            "2019 to 2022",
            "Focused on software engineering and distributed systems.",
            List.of("Java", "Spring Boot", "AWS", "Kafka", "Microservices")
        ));

        resumes.add(createResume(
            "Amelia",
            "Sharma",
            "amelia.sharma@example.com",
            "+91 9998887771",
            "https://portfolio.example.com",
            "Bengaluru, Karnataka",
            "Frontend Engineering Resume",
            "Product Design",
            "Frontend UI Engineer",
            "Designing polished user experiences across React dashboards and accessible product flows.",
            "Infosys",
            "React Frontend Developer",
            "Aug 2022 to Sep 2025",
            "Built responsive UI modules and reusable design components in a fast-moving enterprise product team.",
            "Walmart Labs",
            "UX Engineering Specialist",
            "Jul 2020 to Jul 2022",
            "Partnered with product designers and shipped accessible UI systems across marketplace modules.",
            "Fashion eCommerce Platform",
            "https://example.com/fashion",
            "Jan 2024 to Sep 2025",
            "Led the frontend experience for online storefront personalization and improved conversion journeys.",
            "National Institute of Design",
            "B.Des. Interaction Design",
            "2016 to 2020",
            "Specialized in UI/UX systems, human-centered design, and digital product strategy.",
            List.of("React", "JavaScript", "CSS", "Figma", "Accessibility")
        ));

        resumes.add(createResume(
            "Noah",
            "Wilson",
            "noah.wilson@example.com",
            "+44 7700099911",
            "https://cloudworks.example.com",
            "Manchester, UK",
            "Cloud DevOps Resume",
            "Cloud Operations",
            "Cloud DevOps Specialist",
            "Automating cloud infrastructure and observability for high-availability customer workloads.",
            "ThoughtWorks",
            "DevOps Engineer",
            "Mar 2021 to Present",
            "Implemented CI/CD pipelines, infrastructure automation, and production monitoring across distributed platforms.",
            "Cloud Systems",
            "Platform Reliability Engineer",
            "Jun 2019 to Feb 2021",
            "Maintained observability and release pipelines across customer-facing distributed services.",
            "Container Platform Migration",
            "https://example.com/cloud-migration",
            "Feb 2024 to Present",
            "Migrated customer workloads to a container-first deployment model with secure service mesh routing.",
            "University of Manchester",
            "B.Sc. Computer Science",
            "2015 to 2019",
            "Focused on distributed systems, networking, and software reliability.",
            List.of("Docker", "Kubernetes", "Terraform", "Azure", "Linux")
        ));

        resumes.add(createResume(
            "Priya",
            "Menon",
            "priya.menon@example.com",
            "+91 8877665544",
            "https://dataflow.example.com",
            "Hyderabad, Telangana",
            "Data Platform Resume",
            "Analytics Engineering",
            "Data Platform Analyst",
            "Shaping clean data pipelines and dashboard products for business intelligence and product analytics.",
            "TCS",
            "Data Engineer",
            "Apr 2020 to Aug 2025",
            "Built data ingestion and transformation services that powered executive and operational dashboards.",
            "DataWorks Studio",
            "BI Automation Consultant",
            "Sep 2018 to Mar 2020",
            "Delivered pipeline and reporting automation for retail and digital health customers.",
            "Retail Demand Forecasting",
            "https://example.com/forecasting",
            "Jul 2023 to Aug 2025",
            "Created data quality and forecasting pipelines that supported demand planning and inventory decisions.",
            "Osmania University",
            "M.Sc. Statistics",
            "2017 to 2019",
            "Built a foundation in statistical modeling, analytics, and applied machine learning.",
            List.of("Python", "SQL", "Power BI", "ETL", "Data Modeling")
        ));

        resumes.add(createResume(
            "Liam",
            "Carter",
            "liam.carter@example.com",
            "+1 415 123 4455",
            "https://qualityops.example.com",
            "Austin, Texas",
            "QA Automation Resume",
            "Quality Engineering",
            "QA Automation Lead",
            "Leading regression and automation programs that measure product quality across modern web applications.",
            "Wipro",
            "Automation Test Engineer",
            "Jan 2022 to Present",
            "Designed Selenium and Playwright test suites for enterprise applications with strong API validation coverage.",
            "EIS Digital",
            "Quality Strategy Specialist",
            "May 2019 to Dec 2021",
            "Defined release quality gates and test automation plans for product and support teams.",
            "Digital Workflow Automation",
            "https://example.com/workflow",
            "Oct 2022 to Present",
            "Automated user journeys and service contracts for a digital approvals and workflow product.",
            "University of Texas",
            "B.S. Information Systems",
            "2015 to 2019",
            "Focused on software testing, business systems, and digital transformation.",
            List.of("Selenium", "Playwright", "JUnit", "API Testing", "Test Strategy")
        ));

        resumeRepository.saveAll(resumes);
    }

    private Resume createResume(
        String firstName,
        String lastName,
        String email,
        String phone,
        String website,
        String address,
        String title,
        String currentRole,
        String headline,
        String profSummary,
        String expOneCompany,
        String expOneDesignation,
        String expOneDate,
        String expOneDetails,
        String expTwoCompany,
        String expTwoDesignation,
        String expTwoDate,
        String expTwoDetails,
        String projectTitle,
        String projectUrl,
        String projectDate,
        String projectDetails,
        String institute,
        String degree,
        String educationDate,
        String educationDetails,
        List<String> skillNames
    ) {
        ContactInformation contactInformation = ContactInformation.builder()
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .phone(phone)
            .website(website)
            .address(address)
            .build();

        Summary summary = Summary.builder()
            .headline(headline)
            .profSummary(profSummary)
            .build();

        Resume resume = Resume.builder()
            .title(title)
            .currentRole(currentRole)
            .updated("Updated today")
            .contactInformation(contactInformation)
            .summary(summary)
            .build();

        Experience experience1 = Experience.builder()
            .company(expOneCompany)
            .designation(expOneDesignation)
            .date(expOneDate)
            .details(expOneDetails)
            .resume(resume)
            .build();

        Experience experience2 = Experience.builder()
            .company(expTwoCompany)
            .designation(expTwoDesignation)
            .date(expTwoDate)
            .details(expTwoDetails)
            .resume(resume)
            .build();

        Project project = Project.builder()
            .projectTitle(projectTitle)
            .url(projectUrl)
            .date(projectDate)
            .details(projectDetails)
            .resume(resume)
            .build();

        Education education = Education.builder()
            .institute(institute)
            .degree(degree)
            .date(educationDate)
            .details(educationDetails)
            .resume(resume)
            .build();

        List<Skill> skills = new ArrayList<>();
        for (String skillName : skillNames) {
            skills.add(Skill.builder().name(skillName).resume(resume).build());
        }

        resume.setExperience(List.of(experience1, experience2));
        resume.setProjects(List.of(project));
        resume.setEducation(List.of(education));
        resume.setSkills(skills);

        return resume;
    }
}
