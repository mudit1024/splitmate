package com.splitmate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "authreflector.client.client-secret=test-secret")
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
