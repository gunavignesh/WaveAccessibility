package library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.DataProvider;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DataProviderBean {

	public static WebDriver driver;
	public String Browsr = null;
	Properties properties;

	/*********************************************************/

	// properties file reading

	public void readPropertiesFile(String ElementName) throws Exception {
		try {
			FileInputStream Locator = new FileInputStream(ElementName);
			properties = new Properties();
			properties.load(Locator);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getData(String ElementName) throws Exception {
		String data = properties.getProperty(ElementName);
		return data;
	}

	/*********************************************************/

	// Method for Selecting Desired Browser

	public void SelectBrowser(String BROWSER) throws Exception {

		System.out.println("Browser: " + BROWSER);
		if (BROWSER.equals("FF")) {
			Browsr = "Firefox_";
			System.out.println("Firefox is selected");
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			driver.manage().window().maximize();

		} else if (BROWSER.equals("IE")) {
			Browsr = "IExplore_";
			System.out.println("IE is selected");
			WebDriverManager.iedriver().setup();
			driver = new InternetExplorerDriver();
			driver.manage().window().maximize();
			Thread.sleep(2000);

		} else if (BROWSER.equals("Chrome")) {
			Browsr = "GChrome_";
			System.out.println("Google chrome is selected");
			DesiredCapabilities dc = new DesiredCapabilities();
			dc.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(dc);
			driver.manage().window().maximize();
			Thread.sleep(2000);
		} else {
			System.out.println("Browser not found");
		}
	}

	/*********************************************************/

	// Write Excel File
	public void writeExcel(String filePath, String fileName, String sheetName, String[] dataToWrite)
			throws IOException {
		File file = new File(filePath + "\\" + fileName);
		FileInputStream inputStream = new FileInputStream(file);
		Workbook Workbook = null;
		String fileExtensionName = fileName.substring(fileName.indexOf("."));
		if (fileExtensionName.equals(".xlsx")) {
			Workbook = new XSSFWorkbook(inputStream);
		} else if (fileExtensionName.equals(".xls")) {
			Workbook = new HSSFWorkbook(inputStream);
		}
		Sheet sheet = Workbook.getSheet(sheetName);
		int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
		Row row = sheet.getRow(0);
		Row newRow = sheet.createRow(rowCount + 1);
		for (int j = 0; j < row.getLastCellNum(); j++) {
			Cell cell = newRow.createCell(j);
			cell.setCellValue(dataToWrite[j]);
		}
		inputStream.close();
		FileOutputStream outputStream = new FileOutputStream(file);
		Workbook.write(outputStream);
		outputStream.close();
	}

	/*********************************************************/
	// Compare Excel file

	public String[] compareExcel(String cText, int cLoc, String filePath, String fileName, String sheetName)
			throws IOException {
		File file = new File(filePath + "\\" + fileName);
		DataFormatter formatter = new DataFormatter();
		FileInputStream inputStream = new FileInputStream(file);
		Workbook Workbook = null;
		String fileExtensionName = fileName.substring(fileName.indexOf("."));
		if (fileExtensionName.equals(".xlsx")) {
			Workbook = new XSSFWorkbook(inputStream);
		} else if (fileExtensionName.equals(".xls")) {
			Workbook = new HSSFWorkbook(inputStream);
		}
		Sheet mySheet = Workbook.getSheet(sheetName);
		int rowCount = mySheet.getLastRowNum() - mySheet.getFirstRowNum();

		String cTextret[] = new String[mySheet.getRow(0).getLastCellNum()];

		for (int i = 0; i < rowCount + 1; i++) {
			Row row = mySheet.getRow(i);
			int colCount = row.getLastCellNum();

			if (row.getCell(cLoc).getStringCellValue().trim().equals(cText.trim())) {
				for (int j = 0; j < colCount; j++) {
					cTextret[j] = formatter.formatCellValue(row.getCell(j));
				}
				break;
			}
		}
		return cTextret;
	}

	/*********************************************************/

	// Read Excel
	public String[][] getTableArray(String filePath, String fileName, String sheetName)
			throws IOException {

		File file = new File(filePath + "\\" + fileName);
		FileInputStream inputStream = new FileInputStream(file);
		Workbook Workbook = null;
		String fileExtensionName = fileName.substring(fileName.indexOf("."));

		if (fileExtensionName.equals(".xlsx")) {
			Workbook = new XSSFWorkbook(inputStream);
		}

		else if (fileExtensionName.equals(".xls")) {

			Workbook = new HSSFWorkbook(inputStream);
		}

		Sheet mySheet = Workbook.getSheet(sheetName);
		int startRow, endRow, ci, cj;
		startRow = mySheet.getFirstRowNum();
		endRow = mySheet.getLastRowNum();
		String[][] tabArray = new String[endRow][(mySheet.getRow(startRow).getLastCellNum())];
		ci = 0;

		for (int i = startRow + 1; i <= endRow; i++, ci++) {
			cj = 0;
			Row row = mySheet.getRow(i);
			int colCount = row.getLastCellNum();

			for (int j = 0; j < colCount; j++, cj++) {
				System.out.println(row.getCell(j).getStringCellValue());
				tabArray[ci][cj] = row.getCell(j).getStringCellValue();
			}
		}
		return (tabArray);
	}
	/*********************************************************/

}