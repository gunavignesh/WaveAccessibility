package runner;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import library.DataProviderBean;

public class WaveReport {
	DataProviderBean data = new DataProviderBean();

	public void testReport(String tabName, String secName) throws Throwable {

		String workingDir = System.getProperty("user.dir");
		data.readPropertiesFile(workingDir + "\\src\\test\\Resources\\properties\\config.properties");

		FluentWait<WebDriver> fluentWait = new FluentWait<WebDriver>(data.driver).withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(200, TimeUnit.MILLISECONDS).ignoring(NoSuchElementException.class);
		Thread.sleep(5000);

		while (true) {
			try {
				data.driver.findElement(By.xpath("//*[@id=\"input_url\"]")).click();
				break;
			} catch (UnhandledAlertException f) {
				try {
					Alert alert = data.driver.switchTo().alert();
					String alertText = alert.getText();
					System.out.println("Alert data: " + alertText);
					alert.accept();
				} catch (NoAlertPresentException e) {
					e.printStackTrace();
				}
			}
		}

		data.driver.findElement(By.id(tabName)).click();
		data.driver.findElement(By.id("tab-details")).click();

		fluentWait.until(
				ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"group_list_" + secName + "\"]")));
		WebElement styleTable = data.driver.findElement(By.xpath("//*[@id=\"group_list_" + secName + "\"]"));
		List<WebElement> rows = styleTable.findElements(By.className("icon_type"));
		int count = rows.size();
		System.out.println(count);

		for (int i = 1; i <= count; i++) {
			String captr;
			if (count > 1) {
				captr = data.driver
						.findElement(By.xpath("//*[@id=\"group_list_" + secName + "\"]/li[" + i + "]/h4/label"))
						.getText();
			} else {
				captr = data.driver.findElement(By.xpath("//*[@id=\"group_list_" + secName + "\"]/li/h4/label"))
						.getText();
			}

			System.out.println(captr);
			String captrSplit[] = captr.split(" X ", 2);

			String vKey = captrSplit[1];
			int vCol = Integer.parseInt(data.getData("sColumn"));
			String[] vSeva = data.compareExcel(vKey, vCol, workingDir + data.getData("filePath"),
					data.getData("fileRName"), data.getData("fileSName"));
			String valueToWrite[] = { captrSplit[0], captrSplit[1], vSeva[1], data.driver.getTitle() };
			data.writeExcel(workingDir + data.getData("filePath"), data.getData("fileWName"), data.getData("fileSName"),
					valueToWrite);
		}
	}

	@BeforeTest
	@Parameters({ "BROWSER" })
	public void Browser(String BROWSER) throws Exception {
		data.SelectBrowser(BROWSER);

	}

	@AfterTest
	public void close() throws Exception {
		data.driver.close();
	}

	@Test(dataProvider = "DataFeeder")
	public void waveReport(String wpg) throws Throwable {
		data.driver.get("http://wave.webaim.org");
		data.driver.findElement(By.id("input_url")).click();
		data.driver.findElement(By.id("input_url")).clear();
		data.driver.findElement(By.id("input_url")).sendKeys(wpg);
		data.driver.findElement(By.id("waveform")).submit();
		testReport("contrast", "contrast");
		testReport("styles", "error");
	}

	@DataProvider(name = "DataFeeder")
	public Object[][] GetDataFromExcel() throws Exception {
		data.readPropertiesFile(
				System.getProperty("user.dir") + "\\src\\test\\Resources\\properties\\config.properties");
		Object[][] retObjArr = data.getTableArray(System.getProperty("user.dir") + data.getData("filePath"),
				data.getData("fileRName"), data.getData("inSName"));
		return (retObjArr);
	}
}
