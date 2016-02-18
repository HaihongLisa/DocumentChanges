package IssueExcel;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Initial excels and fetch index for each column
 * The column name is currently working for Jira only.
 * Future work: write a xml for parse other than hardcode
 * @author Haihong Luo
 *
 */
public class ExcelIssues {
	private static String fileLocation;
	private HashMap<String, Integer> columnMap;
	
	public ExcelIssues(String location) {
		this.fileLocation = location;
		columnMap = new HashMap<String, Integer>();
	}
	
	public HashMap<String, Integer> fillColumnMap() {
		try {
			FileInputStream file = new FileInputStream(new File(fileLocation));
			
			//Get the workbook instance for xls file
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			Row row = sheet.getRow(0);
			Iterator<Cell> cellIterator = row.cellIterator();
			int index = 0;
			while(cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				columnMap.put(cell.toString(), index);
				index++;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return columnMap;
	}
}
