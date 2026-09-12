package com.craftsol.service;

import com.craftsol.dto.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ResumePdfGenerator {

    private static final String DEFAULT_TEMPLATE = "orange";
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float LEFT_MARGIN = 50f;
    private static final float RIGHT_MARGIN = 50f;
    private static final float SECTION_TOP_MARGIN = 12f;

    public static int countVisibleSections(ResumeDto resume) {
        if (resume == null) {
            return 0;
        }

        int visibleSections = 0;
        if (hasContactData(resume.getContactInformation())) {
            visibleSections++;
        }
        if (resume.getSummary() != null && hasAnyText(resume.getSummary().getHeadline(), resume.getSummary().getProfSummary())) {
            visibleSections++;
        }
        if (resume.getExperience() != null && !resume.getExperience().isEmpty()) {
            visibleSections++;
        }
        if (resume.getEducation() != null && !resume.getEducation().isEmpty()) {
            visibleSections++;
        }
        if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
            visibleSections++;
        }
        if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
            visibleSections++;
        }
        return visibleSections;
    }

    private static boolean hasContactData(ContactInformationDto contact) {
        return contact != null && hasAnyText(
            contact.getFirstName(), contact.getLastName(), contact.getEmail(),
            contact.getPhone(), contact.getWebsite(), contact.getAddress()
        );
    }

    private static boolean hasAnyText(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return true;
            }
        }
        return false;
    }

    public byte[] generate(ResumeDto resume, String template) {
        Theme theme = resolveTheme(template);
        return renderPdf(resume, theme);
    }

    private Theme resolveTheme(String template) {
        String colorName = template == null || template.isBlank()
            ? DEFAULT_TEMPLATE
            : template.trim().toLowerCase(Locale.ROOT);

        return switch (colorName) {
            case "green" -> new Theme("green", new Color(39, 120, 76), new Color(215, 245, 226), new Color(232, 246, 237), PDType1Font.TIMES_BOLD, PDType1Font.TIMES_ROMAN, PDType1Font.HELVETICA_BOLD);
            case "red" -> new Theme("red", new Color(146, 56, 45), new Color(255, 236, 233), new Color(248, 225, 221), PDType1Font.COURIER_BOLD, PDType1Font.COURIER, PDType1Font.COURIER_BOLD);
            case "purple" -> new Theme("purple", new Color(103, 58, 183), new Color(242, 235, 255), new Color(234, 225, 250), PDType1Font.TIMES_BOLD, PDType1Font.TIMES_ROMAN, PDType1Font.TIMES_BOLD);
            case "black" -> new Theme("black", new Color(30, 30, 30), new Color(244, 244, 244), new Color(232, 232, 232), PDType1Font.HELVETICA_BOLD, PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD);
            case "teal" -> new Theme("teal", new Color(0, 128, 128), new Color(222, 248, 247), new Color(217, 244, 242), PDType1Font.HELVETICA_BOLD, PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD);
            default -> new Theme(DEFAULT_TEMPLATE, new Color(191, 112, 34), new Color(255, 242, 230), new Color(250, 232, 214), PDType1Font.HELVETICA_BOLD, PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD);
        };
    }

    private byte[] renderPdf(ResumeDto resume, Theme theme) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                drawPageFill(stream, theme);
                drawNameCard(stream, resume, theme);
                drawContactLine(stream, resume, theme);

                float y = 640f;
                y = drawProfileSection(stream, resume, theme, y);
                y = drawExperienceSection(stream, resume, theme, y);
                y = drawProjectsSection(stream, resume, theme, y);
                y = drawEducationSection(stream, resume, theme, y);
                y = drawSkillsSection(stream, resume, theme, y);
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to generate resume PDF for template: " + theme.name, ex);
        }
    }

    private void drawPageFill(PDPageContentStream stream, Theme theme) throws IOException {
        stream.setNonStrokingColor(theme.lightColor);
        stream.addRect(0, 0, PAGE_WIDTH, PAGE_HEIGHT);
        stream.fill();

        stream.setNonStrokingColor(theme.primaryColor);
        stream.addRect(0, PAGE_HEIGHT - 16f, PAGE_WIDTH, 16f);
        stream.fill();
    }

    private void drawNameCard(PDPageContentStream stream, ResumeDto resume, Theme theme) throws IOException {
        stream.setNonStrokingColor(new Color(32, 44, 51));
        stream.addRect(45f, 690f, 500f, 74f);
        stream.fill();

        ContactInformationDto contact = resume.getContactInformation();
        String first = safe(contact == null ? null : contact.getFirstName());
        String last = safe(contact == null ? null : contact.getLastName());
        String name = (first + " " + last).trim();
        if (name.isBlank()) {
            name = "Resume";
        }

        String role = safe(resume.getCurrentRole());
        if (role.isBlank()) {
            role = "Software Engineering";
        }

        writeText(stream, theme.titleFont, 24, 70f, 728f, name, Color.WHITE);
        writeText(stream, theme.bodyFont, 11, 70f, 707f, role.toUpperCase(Locale.ROOT), theme.primaryColor);
    }

    private void drawContactLine(PDPageContentStream stream, ResumeDto resume, Theme theme) throws IOException {
        ContactInformationDto contact = resume.getContactInformation();
        if (contact == null) {
            return;
        }
        stream.setStrokingColor(new Color(220,220,220));
        stream.setLineWidth(1f);
        stream.moveTo(50f, 666f);
        stream.lineTo(560f, 666f);
        stream.stroke();

        float x = 50f;
        writeText(stream, theme.bodyFont, 11, x, 654f, safe(contact.getEmail()), new Color(80,80,80));

        writeText(stream, theme.bodyFont, 11, x + 190f, 654f, safe(contact.getPhone()), new Color(80,80,80));

        writeText(stream, theme.bodyFont, 11, x + 360f, 654f, safe(contact.getAddress()), new Color(80,80,80));
    }

    private float drawProfileSection(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        if (resume.getSummary() == null || !hasAnyText(resume.getSummary().getHeadline(), resume.getSummary().getProfSummary())) {
            return y;
        }

        y -= SECTION_TOP_MARGIN;
        drawSectionHeading(stream, "Profile", y, theme);
        y -= 22f;

        String summaryText = safe(resume.getSummary().getHeadline()) + " - " + safe(resume.getSummary().getProfSummary());
        List<String> lines = wrapText(theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, summaryText);
        for (String line : lines) {
            y -= 15f;
            if (y < 50f) {
                return y;
            }
            writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
        }
        return y - 16f;
    }

    private float drawExperienceSection(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        if (resume.getExperience() == null || resume.getExperience().isEmpty()) {
            return y;
        }

        y -= SECTION_TOP_MARGIN;
        drawSectionHeading(stream, "Experience", y, theme);
        y -= 22f;

        for (ExperienceDto experience : resume.getExperience()) {
            String title = safe(experience.getDesignation()) + " at " + safe(experience.getCompany()) + "  " + safe(experience.getDate());
            y -= 16f;
            if (y < 50f) {
                return y;
            }
            writeText(stream, theme.headingFont, 11, LEFT_MARGIN, y, title, theme.primaryColor);

            List<String> lines = wrapText(theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, safe(experience.getDetails()));
            for (String line : lines) {
                y -= 14f;
                if (y < 50f) {
                    return y;
                }
                writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
            }
            y -= 16f;
        }
        return y - 12f;
    }

    private float drawProjectsSection(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        if (resume.getProjects() == null || resume.getProjects().isEmpty()) {
            return y;
        }

        y -= SECTION_TOP_MARGIN;
        drawSectionHeading(stream, "Projects", y, theme);
        y -= 22f;

        for (ProjectDto project : resume.getProjects()) {
            String title = safe(project.getProjectTitle()) + " (" + safe(project.getDate()) + ")";
            y -= 16f;
            if (y < 50f) {
                return y;
            }
            writeText(stream, theme.headingFont, 11, LEFT_MARGIN, y, title, theme.primaryColor);

            List<String> urlLines = wrapText(theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, safe(project.getUrl()));
            for (String line : urlLines) {
                y -= 14f;
                if (y < 50f) {
                    return y;
                }
                writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
            }

            List<String> detailLines = wrapText(theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, safe(project.getDetails()));
            for (String line : detailLines) {
                y -= 14f;
                if (y < 50f) {
                    return y;
                }
                writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
            }
            y -= 16f;
        }
        return y - 12f;
    }

    private float drawEducationSection(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        if (resume.getEducation() == null || resume.getEducation().isEmpty()) {
            return y;
        }

        y -= SECTION_TOP_MARGIN;
        drawSectionHeading(stream, "Education", y, theme);
        y -= 22f;

        for (EducationDto education : resume.getEducation()) {
            String title = safe(education.getDegree()) + " - " + safe(education.getInstitute()) + " (" + safe(education.getDate()) + ")";
            y -= 16f;
            if (y < 50f) {
                return y;
            }
            writeText(stream, theme.headingFont, 11, LEFT_MARGIN, y, title, theme.primaryColor);

            List<String> detailLines = wrapText(theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, safe(education.getDetails()));
            for (String line : detailLines) {
                y -= 14f;
                if (y < 50f) {
                    return y;
                }
                writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
            }
            y -= 16f;
        }
        return y - 12f;
    }

    private float drawSkillsSection(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        if (resume.getSkills() == null || resume.getSkills().isEmpty()) {
            return y;
        }

        y -= SECTION_TOP_MARGIN;
        drawSectionHeading(stream, "Skills", y, theme);
        y -= 22f;

        String skillsLine = String.join(", ", resume.getSkills());
        List<String> lines = wrapText(theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, skillsLine);
        for (String line : lines) {
            y -= 14f;
            if (y < 50f) {
                return y;
            }
            writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
        }
        return y - 16f;
    }

    private void drawSectionHeading(PDPageContentStream stream, String title, float y, Theme theme) throws IOException {
        writeText(stream, theme.headingFont, 14, 50f, y, title, theme.primaryColor);
        stream.setStrokingColor(new Color(200,200,200));
        stream.setLineWidth(1f);
        stream.moveTo(50f, y - 8f);
        stream.lineTo(560f, y - 8f);
        stream.stroke();
    }

    private void writeText(PDPageContentStream stream, PDFont font, float size, float x, float y, String value, Color color) throws IOException {
        stream.setNonStrokingColor(color);
        stream.setFont(font, size);
        stream.beginText();
        stream.newLineAtOffset(x, y);
        stream.showText(safe(value));
        stream.endText();
    }

    private List<String> wrapText(PDFont font, float size, float lineWidth, String text) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return lines;
        }

        String[] words = text.split("\\s+");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            float candidateWidth = font.getStringWidth(candidate) / 1000f * size;
            if (candidateWidth > lineWidth && !current.isEmpty()) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }

        if (!current.isEmpty()) {
            lines.add(current.toString());
        }

        return lines;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private record Theme(String name, Color primaryColor, Color lightColor, Color fillColor, PDFont titleFont, PDFont bodyFont, PDFont headingFont) {
    }
}
