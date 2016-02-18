package ReleaseNotesLink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import Policies.ByType;

/**
 * Get changes based on type user entered. The default policy must be called first, it will filter based on default policy's result
 * It override ByType function and return HashMap<HashMap<String, HashMap<String, String>>>
 * 	Key: version value: release notes map for that version with key: id, value: summary
 * @author Haihong Luo
 *
 */
public class WebByType implements ByType {
	private HashMap<String, Integer> columnMap;
	
	public WebByType(String token, String version1, String version2) throws Exception {
		ReleaseNotes releaseNotes = new ReleaseNotes(token);
		releaseNotes.createExcelForChanges(version1, version2);
	}
	
	@Override
	public HashMap<String, HashMap<String, String>> fetchChangesByType(String types)
			throws Exception {
		HashMap<String, HashMap<String, String>> releaseNotesMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> versionMap;
		columnMap = new HashMap<String, Integer>();
		try {
			FileInputStream file = new FileInputStream(new File("ReleaseNotes.xls"));
			
			//Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			//Get the sheet from the workbook
			HSSFSheet sheet = workbook.getSheet("ReleaseNotes");
			
			Row row = sheet.getRow(0);
			Iterator<Cell> cellIterator = row.cellIterator();
			int index = 0;
			while(cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				columnMap.put(cell.toString(), index);
				index++;
			}
			//Iterate through each rows from ReleaseNotes sheet
			int rowLen = sheet.getLastRowNum();
			for(int i = 1; i <= rowLen; i++) {
				int typeIndex = columnMap.get("Issue Type");
				Cell type = sheet.getRow(i).getCell(typeIndex); 
				
				if(!type.toString().equals(types)) {
					continue;
				}
				
				int versionIndex = columnMap.get("Fix Version/s");
				Cell version = sheet.getRow(i).getCell(versionIndex);
				int keyIndex = columnMap.get("Key");
				Cell key = sheet.getRow(i).getCell(keyIndex);
				int summaryIndex = columnMap.get("Summary");
				Cell summary = sheet.getRow(i).getCell(summaryIndex);
				
				if(releaseNotesMap.containsKey(version.toString())) {
					versionMap = releaseNotesMap.get(version.toString());
				} else {
					versionMap = new HashMap<String, String>();
				}
				versionMap.put(key.toString(), summary.toString());
				releaseNotesMap.put(version.toString(), versionMap);
				
			}
			file.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return releaseNotesMap;
	}
}
