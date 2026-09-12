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

    private static final String DEFAULT_TEMPLATE = "blue";
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float LEFT_MARGIN = 50f;
    private static final float RIGHT_MARGIN = 50f;
    private static final float TOP_MARGIN = 50f;
    private static final float SECTION_GAP = 16f;

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
            case "orange" -> new Theme("orange", new Color(191, 112, 34), new Color(255, 242, 230), new Color(250, 232, 214), PDType1Font.HELVETICA_BOLD_OBLIQUE, PDType1Font.HELVETICA_OBLIQUE, PDType1Font.HELVETICA_BOLD);
            case "purple" -> new Theme("purple", new Color(103, 58, 183), new Color(242, 235, 255), new Color(234, 225, 250), PDType1Font.TIMES_BOLD, PDType1Font.TIMES_ROMAN, PDType1Font.TIMES_BOLD);
            case "black" -> new Theme("black", new Color(30, 30, 30), new Color(244, 244, 244), new Color(232, 232, 232), PDType1Font.HELVETICA_BOLD, PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD);
            case "teal" -> new Theme("teal", new Color(0, 128, 128), new Color(222, 248, 247), new Color(217, 244, 242), PDType1Font.HELVETICA_BOLD, PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD);
            default -> new Theme("blue", new Color(21, 90, 154), new Color(230, 240, 250), new Color(223, 236, 247), PDType1Font.HELVETICA_BOLD, PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD);
        };
    }

    private byte[] renderPdf(ResumeDto resume, Theme theme) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                drawPageFill(stream, theme);
                drawHeader(stream, resume, theme);
                float y = 720f;

                y = renderContact(stream, resume, theme, y);
                y = renderSummary(stream, resume, theme, y);
                y = renderExperience(stream, resume, theme, y);
                y = renderEducation(stream, resume, theme, y);
                y = renderProjects(stream, resume, theme, y);
                y = renderSkills(stream, resume, theme, y);
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

    private void drawHeader(PDPageContentStream stream, ResumeDto resume, Theme theme) throws IOException {
        stream.setNonStrokingColor(theme.primaryColor);
        stream.addRect(LEFT_MARGIN, 770f, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, 34f);
        stream.fill();

        stream.setNonStrokingColor(Color.WHITE);
        writeText(stream, theme.titleFont, 22, LEFT_MARGIN + 2f, 780f, safe(resume.getTitle()), Color.WHITE);

        stream.setStrokingColor(theme.primaryColor);
        stream.setLineWidth(1f);
        stream.moveTo(LEFT_MARGIN, 760f);
        stream.lineTo(PAGE_WIDTH - RIGHT_MARGIN, 760f);
        stream.stroke();
    }

    private float renderContact(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        ContactInformationDto contact = resume.getContactInformation();
        if (contact == null) {
            return y;
        }

        String contactLine = safe(contact.getFirstName()) + " " + safe(contact.getLastName())
            + " | " + safe(contact.getEmail())
            + " | " + safe(contact.getPhone())
            + " | " + safe(contact.getWebsite())
            + " | " + safe(contact.getAddress());

        writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, contactLine, theme.primaryColor);
        return y - 24f;
    }

    private float renderSummary(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        if (resume.getSummary() == null) {
            return y;
        }

        SummaryDto summary = resume.getSummary();
        writeText(stream, theme.headingFont, 12, LEFT_MARGIN, y, "Summary", theme.primaryColor);
        y -= 20f;

        String summaryText = safe(summary.getHeadline()) + " - " + safe(summary.getProfSummary());
        List<String> lines = wrapText(stream, theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, summaryText);
        for (String line : lines) {
            y -= 16f;
            if (y < 60) {
                return y;
            }
            writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
        }
        return y - 20f;
    }

    private float renderExperience(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        if (resume.getExperience() == null || resume.getExperience().isEmpty()) {
            return y;
        }

        writeText(stream, theme.headingFont, 12, LEFT_MARGIN, y, "Experience", theme.primaryColor);
        y -= 22f;

        for (ExperienceDto experience : resume.getExperience()) {
            String title = safe(experience.getDesignation()) + " at " + safe(experience.getCompany()) + " (" + safe(experience.getDate()) + ")";
            writeText(stream, theme.headingFont, 11, LEFT_MARGIN, y, title, theme.primaryColor);
            y -= 18f;

            List<String> lines = wrapText(stream, theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, safe(experience.getDetails()));
            for (String line : lines) {
                y -= 16f;
                if (y < 60) {
                    return y;
                }
                writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
            }
            y -= 12f;
        }
        return y;
    }

    private float renderEducation(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        if (resume.getEducation() == null || resume.getEducation().isEmpty()) {
            return y;
        }

        writeText(stream, theme.headingFont, 12, LEFT_MARGIN, y, "Education", theme.primaryColor);
        y -= 22f;

        for (EducationDto education : resume.getEducation()) {
            String title = safe(education.getDegree()) + " - " + safe(education.getInstitute()) + " (" + safe(education.getDate()) + ")";
            writeText(stream, theme.headingFont, 11, LEFT_MARGIN, y, title, theme.primaryColor);
            y -= 18f;

            List<String> lines = wrapText(stream, theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, safe(education.getDetails()));
            for (String line : lines) {
                y -= 16f;
                if (y < 60) {
                    return y;
                }
                writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
            }
            y -= 12f;
        }
        return y;
    }

    private float renderProjects(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        if (resume.getProjects() == null || resume.getProjects().isEmpty()) {
            return y;
        }

        writeText(stream, theme.headingFont, 12, LEFT_MARGIN, y, "Projects", theme.primaryColor);
        y -= 22f;

        for (ProjectDto project : resume.getProjects()) {
            String title = safe(project.getProjectTitle()) + " (" + safe(project.getDate()) + ")";
            writeText(stream, theme.headingFont, 11, LEFT_MARGIN, y, title, theme.primaryColor);
            y -= 18f;

            List<String> lines = wrapText(stream, theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, safe(project.getUrl()));
            for (String line : lines) {
                y -= 16f;
                if (y < 60) {
                    return y;
                }
                writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
            }

            lines = wrapText(stream, theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, safe(project.getDetails()));
            for (String line : lines) {
                y -= 16f;
                if (y < 60) {
                    return y;
                }
                writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
            }
            y -= 12f;
        }
        return y;
    }

    private float renderSkills(PDPageContentStream stream, ResumeDto resume, Theme theme, float y) throws IOException {
        if (resume.getSkills() == null || resume.getSkills().isEmpty()) {
            return y;
        }

        writeText(stream, theme.headingFont, 12, LEFT_MARGIN, y, "Skills", theme.primaryColor);
        y -= 20f;
        List<String> lines = wrapText(stream, theme.bodyFont, 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN, String.join(", ", resume.getSkills()));
        for (String line : lines) {
            y -= 16f;
            if (y < 60) {
                return y;
            }
            writeText(stream, theme.bodyFont, 10, LEFT_MARGIN, y, line, Color.DARK_GRAY);
        }
        return y;
    }

    private void writeText(PDPageContentStream stream, PDFont font, float size, float x, float y, String value, Color color) throws IOException {
        stream.setNonStrokingColor(color);
        stream.setFont(font, size);
        stream.beginText();
        stream.newLineAtOffset(x, y);
        stream.showText(safe(value));
        stream.endText();
    }

    private List<String> wrapText(PDPageContentStream stream, PDFont font, float size, float lineWidth, String text) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return lines;
        }

        String[] words = text.split("\\s+");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String candidate = current.length() == 0 ? word : current + " " + word;
            float candidateWidth = font.getStringWidth(candidate) / 1000f * size;
            if (candidateWidth > lineWidth && current.length() > 0) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }

        if (current.length() > 0) {
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
