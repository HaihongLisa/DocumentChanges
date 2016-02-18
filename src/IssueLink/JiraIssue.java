package IssueLink;

import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Get all versiosn list from Jira, it is basic function for Jira policies
 * 	Each jira policy need call this version first to get versions list
 * @author Haihong Luo
 *
 */
public class JiraIssue {
	private static HashMap<String, String> versionURL;
	private String baseURL = "https://issues.apache.org/jira/browse/";
	private static String token;
	
	public JiraIssue(String token) {
		versionURL = new HashMap<String, String>();
		this.token = token;
	}
	
	
	public HashMap<String, String> getVersionURL() {
		return versionURL;
	}
	
	public String getToken() {
		return token;
	}
	
	/**
	 * Get all versions list from Jira
	 * @param token
	 * 		project name, for example, CASSANDRA
	 * @throws Exception
	 */
	public void getLinks() throws Exception {
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
	}
}
