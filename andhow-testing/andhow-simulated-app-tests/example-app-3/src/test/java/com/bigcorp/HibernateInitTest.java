package com.bigcorp;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeEachTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

/**
 * These simple tests don't do much - just show how it might be setup
 */
@KillAndHowBeforeEachTest
class HibernateInitTest {

	@Test
	void getHibernateProperties() throws IllegalAccessException {

		HibernateInit hibernateInit = new HibernateInit();
		HibernateInit.HibernateConfiguration hc = hibernateInit.pretendToinitializeHibernate();

		Map<String, String> export = hc.getProperties().entrySet().stream()
				.collect(Collectors.toMap(
					e -> e.getKey().toString(),
					e -> (e.getValue() != null)?e.getValue().toString():(String)null
		));

		assertThat(export, hasEntry("hibernate.connection.username", "carl"));
		assertThat(export, hasEntry("hibernate.connection.password", "abcd"));
		assertThat(export, hasEntry("hibernate.connection.initial_pool_size", "2"));
		assertThat(export, hasEntry("hibernate.connection.url", "jdbc:postgresql:hibernate_orm_test"));
		assertThat(export, hasEntry("hibernate.connection.isolation", "TRANSACTION_REPEATABLE_READ"));
		assertThat(export, hasEntry("hibernate.connection.driver_class", "org.postgresql.Driver"));
		assertThat(export, hasEntry("hibernate.connection.pool_size", "10"));
		assertEquals(7, export.size());
	}

	@Test
	void pretendToinitializeHibernate() throws IllegalAccessException {
		HibernateInit hibernateInit = new HibernateInit();
		HibernateInit.HibernateConfiguration hc = hibernateInit.pretendToinitializeHibernate();

		assertEquals(7, hc.getProperties().size());
	}

	@Test
	void main() throws Exception {
		HibernateInit.main(new String[] {});
	}
}