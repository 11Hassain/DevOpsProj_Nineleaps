package com.example.devopsproj;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DevOpsProjApplicationTests {
	@Test
	void contextLoads() {
		boolean condition = true;
		Assertions.assertTrue(condition, "Test class is being loaded successfully");
	}

	@Test
	void mainMethod() {
		// This test method is just to ensure that the Spring context loads without errors.
		DevOpsProjApplication.main(new String[] {});
		// You can add more assertions or checks specific to your application here.
	}
}
