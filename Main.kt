package Testng;

import java.util.List;

//import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;

public class Testng {

	public static void main(String[] args) {
		//TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		suites.add("C:/Users/maarten.burrei/workspace/gtbv/src/main/resources/testng.xml");//path to xml..
 		testng.setTestSuites(suites);
		testng.run();

	}

}