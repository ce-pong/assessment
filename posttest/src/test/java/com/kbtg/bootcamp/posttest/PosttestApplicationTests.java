package com.kbtg.bootcamp.posttest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {PosttestApplicationTests.class})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class PosttestApplicationTests {

	@Test
	void contextLoads() {
	}

}

