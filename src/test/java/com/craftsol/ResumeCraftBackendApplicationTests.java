package com.craftsol;

import com.craftsol.dto.ContactInformationDto;
import com.craftsol.dto.ResumeDto;
import com.craftsol.dto.SummaryDto;
import com.craftsol.service.ResumePdfGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ResumeCraftBackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void countVisibleSectionsReflectsResumeObjectData() {
		ResumeDto resume = ResumeDto.builder()
			.contactInformation(ContactInformationDto.builder()
				.firstName("Jane")
				.lastName("Doe")
				.email("jane@example.com")
				.phone("+1 555 123 1234")
				.website("https://example.com")
				.address("Austin, TX")
				.build())
			.summary(SummaryDto.builder()
				.headline("Senior Product Designer")
				.profSummary("Designing user-focused products.")
				.build())
			.build();

		assertEquals(2, ResumePdfGenerator.countVisibleSections(resume));
	}

	@Test
	void defaultTemplateThemeResolvesToOrangeDesign() throws Exception {
		ResumePdfGenerator generator = new ResumePdfGenerator();
		Method resolveTheme = ResumePdfGenerator.class.getDeclaredMethod("resolveTheme", String.class);
		resolveTheme.setAccessible(true);
		Object theme = resolveTheme.invoke(generator, (String) null);

		assertEquals("orange", theme.getClass().getMethod("name").invoke(theme));
	}

}
