package IssueExcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import Policies.ByKeyWord;

public class ExcelByKeyWord implements ByKeyWord{
	private static String fileLocation;
	private HashMap<String, Integer> columnMap;
	private String version1;
	private String version2;
	
	public ExcelByKeyWord(String location, String verison1, String version2) {
		this.fileLocation = location;
		this.version1 = verison1;
		this.version2 = version2;
		ExcelIssues excel = new ExcelIssues(location);
		columnMap = excel.fillColumnMap();
	}
	

	@Override
	public HashMap<String, HashMap<String, String>> fetchChangesByKeyWord(String word) throws Exception {
		HashMap<String, HashMap<String, String>> issuesMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> versionMap;
		
		try {
			FileInputStream file = new FileInputStream(new File(fileLocation));
			
			//Get the workbook instance for xls file
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			//Get the sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			int rowLen = sheet.getLastRowNum();
			for(int i = 1; i <= rowLen; i++) {
				int versionIndex = columnMap.get("Fix Version/s");
				String version = sheet.getRow(i).getCell(versionIndex).toString();
				if(compareVersion(version, version1) > 0 && compareVersion(version, version2) <= 0) {
					int descIndex = columnMap.get("Description");
					String descrpiton = sheet.getRow(i).getCell(descIndex).toString();
					
					int summaryIndex = columnMap.get("Summary");
					String summary = sheet.getRow(i).getCell(summaryIndex).toString();
					if(!descrpiton.toLowerCase().contains(word.toLowerCase()) && !summary.toLowerCase().contains(word.toLowerCase())) {
						continue;
					}
					
					
					int keyIndex = columnMap.get("Key");
					String key = sheet.getRow(i).getCell(keyIndex).toString();
					
					if(issuesMap.containsKey(version)) {
						versionMap = issuesMap.get(version);
					} else {
						versionMap = new HashMap<String, String>();
					}
					versionMap.put(key, summary);
					issuesMap.put(version, versionMap);
				}
			}
			file.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return issuesMap;
	}
	
	
	/* Compare whether the two version is same or not
	 * @param: 
	 * 	 version1: the key in the hashmap
	 *   version2 is the version user entered or between two versions
	 * 				(for example, user enter "2.1.3", "2.1.0", the possible value will be <= 2.1.3 and >= 2.1.0 )
	 */
    public int compareVersion(String version1, String version2) {
        if(version1 == null || version2 == null || version1.equals(version2))    return 0;
        
        int index1 = 0;
        int index2 = 0;
        while(index1 < version1.length() || index1 < version2.length()) {
            int sum1 = 0;
            int sum2 = 0;
            while(index1 < version1.length() && version1.charAt(index1) != '.') {
                sum1 = sum1 * 10 + (version1.charAt(index1) - '0');
                index1++;
            }
            
            while(index2 < version2.length() && version2.charAt(index2) != '.') {
                sum2 = sum2 * 10 + (version2.charAt(index2) - '0');
                index2++;
            }
            
            if(sum1 > sum2) return 1;
            if(sum1 < sum2) return -1;
            index1++;
            index2++;
        }
        return 0;
    }
}
