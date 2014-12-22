package com.testng.tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;

import com.testng.tests.mock.MockTests4;

public class MyTestsFactory {

	@Factory(dataProvider = "dp")
	public Object[] createInstances(int id, String account) {
		return new Object[] { new MockTests4() };
	}

	@DataProvider(name = "dp")
	public static Object[][] dataProvider() {
		Object[][] dataArray = { { 1, "user1" }, { 2, "user2" } };
		return dataArray;
	}
}
