package Policies;

import java.util.HashMap;

public interface ByTimeStamp {
	/**
	 * Fetch changes for specific timeStamp between two versions
	 * @param timeStamp
	 * 	The time format should be as similar as "02-Dec-2015" in other to match Jira
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, HashMap<String, String>> fetchChangesByTimeStamp(String timeStamp) throws Exception;
}
