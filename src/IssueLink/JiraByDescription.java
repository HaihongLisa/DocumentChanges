package IssueLink;

import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Policies.ByDescription;

public class JiraByDescription implements ByDescription {
	private static HashMap<String, String> versionURL;
	private String baseURL = "https://issues.apache.org/jira/browse/";
	private String base = "https://issues.apache.org/jira/issues/?jql=";
	private String projectName = "";
	
	public JiraByDescription() {
		versionURL = new HashMap<String, String>();
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public HashMap<String, String> getVersionURL() {
		return versionURL;
	}
	
	/**
	 * Get all versions list from Jira
	 * @param token
	 * 		project name, for example, CASSANDRA
	 * @throws Exception
	 */
	public void getLinks(String token) throws Exception {
		projectName = token;
		String url = baseURL + token;
		//Get project jira link
		Document doc = Jsoup.connect(url).get();
		String versionLink = doc.select("a[id=com.atlassian.jira.jira-projects-plugin:versions-panel-panel]").first().absUrl("href") + "&subset=-1";
		
		//Get all versions
		Document allVersions = Jsoup.connect(versionLink).get();
		Elements versionList = allVersions.select("a[class=summary]");
		
		
		for(Element element : versionList) {
			String version = element.text();
			String link = element.attr("href");
			versionURL.put(version, link);
		}
		
		//For debug purpose
//		for(String key : versionURL.keySet()) {
//			System.out.println(key + "    " + versionURL.get(key));
//		}
		
	}
	
	public HashMap<String, HashMap<String, String>> fetchChangesByDescription(String version1, String version2) throws Exception {
		HashMap<String, HashMap<String, String>> issuesMap = new HashMap<String, HashMap<String, String>>();
		if(!versionURL.containsKey(version1) || !versionURL.containsKey(version2)) {
			throw new IllegalArgumentException("Cannot find related version in the Jira versions list, please select another version");
		}
		
		for(String version : versionURL.keySet()) {
			if(compareVersion(version,version1) > 0 && compareVersion(version, version2) <= 0) {
				HashMap<String, String> map = new HashMap<>();
				//scrape all issues for that version	
				String url = base + "fixVersion+%3D+" + version + "+AND+project+%3D+" + projectName;
				Document doc = Jsoup.connect(url).get();
				Elements links = doc.select("li[data-key]");
				for(Element element : links) {
					String id = element.attr("data-key");
					String descr = element.attr("title");
					map.put(id, descr);
				}
				issuesMap.put(version, map);
			}
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
