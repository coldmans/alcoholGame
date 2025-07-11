package com.example.alcoholGameBackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
		"webpush.enabled=false"
})
class AlcoholGameBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
