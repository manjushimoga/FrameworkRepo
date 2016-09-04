package delta.main;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.xml.XmlTest;

import com.relevantcodes.extentreports.LogStatus;

import generics.Excel;
import generics.Property;
import generics.Utility;

public class DeltaDriver extends BaseDriver{
		public String browser;
	@BeforeMethod
	public void launchApp(XmlTest xmlTest){
	    	
		 browser=xmlTest.getParameter("browser");
		if(browser.equals("chrome")){
			System.setProperty("webdriver.chrome.driver", "./exeFiles/chromedriver.exe");
			driver=new ChromeDriver();
		}else{
		driver = new FirefoxDriver();
		}
		
		String appURL=Property.getPropertyValue(configPptPath, "URL");
		System.out.println(appURL);
		String timeout=Property.getPropertyValue(configPptPath, "TimeOut");
		driver.get(appURL);
		driver.manage().timeouts().implicitlyWait(Long.parseLong(timeout), TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}// end of launchApp method
	
	
	@Test(dataProvider="getScenario")
	public void testScenarios(String scenarioSheet,String executionStatus){
	      // scenarioPath="./Scripts/Scenarios.xlsx";
		testReport = eReport.startTest(browser+"_"+scenarioSheet);
		System.out.println(executionStatus);
		if(executionStatus.equalsIgnoreCase("yes")){
			int stepCount=Excel.getRowCount(scenarioPath, scenarioSheet);
			for(int i=1;i<=stepCount;i++){
			String description=Excel.getCellValue(scenarioPath, scenarioSheet, i, 0);
			String action=Excel.getCellValue(scenarioPath, scenarioSheet, i, 1);
			String input1=Excel.getCellValue(scenarioPath, scenarioSheet, i, 2);
			String input2=Excel.getCellValue(scenarioPath, scenarioSheet, i, 3);
			
			String msg="description:"+description+"action:"+action+"input1:"+input1+"input2:"+input2;
			testReport.log(LogStatus.INFO, msg);
			
			Keyword.executeKeyword(driver, action, input1, input2);
//			Assert.fail();
		}
				
	}//end of for loop	
		else
		{
			testReport.log(LogStatus.SKIP, "Execution Status is 'No'");
			throw new SkipException("Skiping this exception");
		}// end of if condition
		
	}// end of testScenarios method
	
	@AfterMethod
	public void quitApp(ITestResult test){
		if(test.getStatus()==2){
			
			String pImage=Utility.getPageScreenShot(driver, imageFolderPath);
			String p=testReport.addScreenCapture("."+pImage);
			testReport.log(LogStatus.FAIL, "Page Screenshot : "+p);
		}
		eReport.endTest(testReport);
		driver.close();
		
	}// end of quitApp method
	
	
}// end of class DeltaDriver
