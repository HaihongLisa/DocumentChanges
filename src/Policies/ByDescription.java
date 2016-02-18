package Policies;

import java.util.HashMap;

public interface ByDescription {
	/**
	 * Fetch changes for two versions, default policy
	 * @param verison1
	 * 	The earlier version
	 * @param version2
	 * 	The later version
	 * @return
	 * 	HashMap<String, HashMap<String, String>>
	 * 		Key: version number, value: Issue Key, Issue Summary
	 * @throws Exception
	 */
	public HashMap<String, HashMap<String, String>> fetchChangesByDescription(String verison1, String version2) throws Exception;
}
