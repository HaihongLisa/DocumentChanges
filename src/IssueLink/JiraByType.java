package IssueLink;

import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Policies.ByType;

/**
 * Jira by issue type policy, by which it gets changes the specific type
 * It override ByType function and return HashMap<HashMap<String, HashMap<String, String>>>
 * 	Key: version value: release notes map for that version with key: id, value: summary
 * @author Haihong Luo
 *
 */
public class JiraByType implements ByType {
	HashMap<String, String> versionURL;
	private String base = "https://issues.apache.org/jira/issues/?jql=";
	private String token;
	private String version1;
	private String version2;
	
	
	/**
	 * Call JiraIssue functions to construct versionURL
	 * @param token
	 * 	Project name such as CASSANDRA
	 * @param type
	 * 	Jira issue type
	 * @throws Exception
	 */
	public JiraByType(String token, String version1, String version2, String type) throws Exception {
		this.token = token;
		this.version1 = version1;
		this.version2 = version2;
		JiraIssue jiraissue = new JiraIssue(token);
		jiraissue.getLinks();
		versionURL = jiraissue.getVersionURL();
	}
	
	/**
	 * Fetch changes by jira issue type
	 * @return
	 * 	HashMap<String, HashMap<String, String>>
	 * 	Key: version
	 * 	Value: Issue list map for that version with key: Jira Issue ID and value: Jira Issue Description
	 */
	@Override
	public HashMap<String, HashMap<String, String>> fetchChangesByType(String type) throws Exception {
		HashMap<String, HashMap<String, String>> issueTypeMap = new HashMap<String, HashMap<String, String>>();
		for(String version: versionURL.keySet()) {
			if(compareVersion(version,version1) > 0 && compareVersion(version, version2) <= 0) {
				HashMap<String, String> map = new HashMap<>();
				//Hardcode for different pages according to Jira Rules since cannot find next page link using Jsoup for Jira
				for(int i = 0; i < 10; i++) {
					int index = 50 * i;
					String page = base + "fixVersion+%3D+" + version + "+AND+project+%3D+" + token + "&startIndex=" + index;
					Document doc = Jsoup.connect(page).get();
					
					//For each page get issue list
					Elements links = doc.select("li[data-key]");
					for(Element element : links) {
						//Fetch issueType
						String issueType = element.select("img").first().attr("alt");
						String id = element.attr("data-key");
						String descr = element.attr("title");
	
						if(issueType.equals(type)) {
							map.put(id, descr);
						}
					}
				}
				issueTypeMap.put(version, map);
			}
		}
		return issueTypeMap;
	}
		
		
		/** Compare whether the two version is same or not
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
