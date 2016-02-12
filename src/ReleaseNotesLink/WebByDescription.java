package ReleaseNotesLink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import IssueLink.JiraByDescription;

public class WebByDescription {
	private HashMap<String, String> versions;
	private String baseURL = "https://issues.apache.org";
	private int count = 1;
	
	public WebByDescription() {
		versions = new HashMap<String, String>();
		
	}
	
	public void getReleaseNotesContent(String token) throws Exception {
		JiraByDescription versionList = new JiraByDescription();
		versionList.getLinks(token);
		versions = versionList.getVersionURL();
	} 
	
	
	/**
	 * Fill changes into excel
	 * It has 4 columns: Version, Type, ID, Description
	 * @param version1
	 * 	The earlier version user input
	 * @param version2
	 * 	The later version user input
	 */
	public void createExcelForChanges(String version1, String version2){
		createReleaseNotesExcel();
		if(!versions.containsKey(version1) || !versions.containsKey(version2)) {
			throw new IllegalArgumentException("Cannot find related version in the Jira versions list, please select another version");
		}
		
		try {
			FileInputStream file = new FileInputStream(new File("ReleaseNotes.xls"));
			
			//Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(file);
			//Get the sheet from the workbook
			HSSFSheet sheet = workbook.getSheet("ReleaseNotes");
			for(String version : versions.keySet()) {
				if(compareVersion(version,version1) > 0 && compareVersion(version, version2) <= 0) {
					String url = baseURL + versions.get(version);
					Document doc = Jsoup.connect(url).get();
					String releaseNotesLink = doc.select("a[id=release-notes-lnk]").first().absUrl("href");
					Document releaseNotes = Jsoup.connect(releaseNotesLink).get();
	
					String[] strs = releaseNotes.text().split("\\n");
					boolean sameType = false;
					String type = "";
					for(String str : strs) {
						if(str.contains("<h2>")) {
							type = str.substring(12);
							if(type.equals("Edit/Copy Release Notes")) {
								break;
							}
							sameType = true;
						}
						
						
						//The index is based on CASSANDRA specific release Notes
						if(sameType) {
							int start = str.lastIndexOf("'>");
							int end = str.lastIndexOf("</a>");
							if(start != -1 && end != -1) {
								Row row = sheet.createRow(count);
								Cell cell0 = row.createCell(0);
								//Fill version
								cell0.setCellValue(version);
								
								//Fill type
								Cell cell1 = row.createCell(1);
								cell1.setCellValue(type);
								
								//Fill id
								String id = str.substring(start + 2, end);
								Cell cell2 = row.createCell(2);
								cell2.setCellValue(id);
								
								//Fill description
								String descr = str.substring(end + 16);
								Cell cell3 = row.createCell(3);
								cell3.setCellValue(descr);
								count++;
							}
						}
					}
				}
			}
			file.close();
			FileOutputStream out = new FileOutputStream(new File("ReleaseNotes.xls"));
			workbook.write(out);
			out.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
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
		
    /**
     * Create Excel for fill data
     */
	private void createReleaseNotesExcel() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("ReleaseNotes");
		//Create a new row in current sheet
		Row row = sheet.createRow(0);
		//Create a new cell in current row;
		Cell cell0 = row.createCell(0);
		Cell cell1 = row.createCell(1);
		Cell cell2 = row.createCell(2);
		Cell cell3 = row.createCell(3);
		cell0.setCellValue("Fix Version/s");
		cell1.setCellValue("Issue Type");
		cell2.setCellValue("Key");
		cell3.setCellValue("Summary");
		
		try{
			FileOutputStream out = new FileOutputStream(new File("ReleaseNotes.xls"));
			workbook.write(out);
			out.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		WebByDescription test = new WebByDescription();
		test.getReleaseNotesContent("CASSANDRA");
		test.createExcelForChanges("2.1.1", "2.1.3");
	}
}
