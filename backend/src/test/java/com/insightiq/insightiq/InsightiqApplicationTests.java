package com.insightiq.insightiq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "CLOUDINARY_CLOUD_NAME=dummy",
    "CLOUDINARY_API_KEY=dummy",
    "CLOUDINARY_API_SECRET=dummy",
    "GROQ_API_KEY=dummy"
})
class InsightiqApplicationTests {

	@Test
	void contextLoads() {
	}

}
