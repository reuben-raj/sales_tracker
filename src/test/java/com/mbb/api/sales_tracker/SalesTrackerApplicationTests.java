package com.mbb.api.sales_tracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("unit")
class SalesTrackerApplicationTests {

	@Test
	void testMain() {
		System.setProperty("spring.profiles.active", "unit");

		SalesTrackerApplication.main(new String[] {});
	}

}
