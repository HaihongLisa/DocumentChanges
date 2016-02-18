package Policies;

import java.util.HashMap;

public interface ByType {
	/**
	 * Fetch changes for specific type between two versions
	 * @param type
	 * 	for example, issue types.
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, HashMap<String, String>> fetchChangesByType(String type) throws Exception;
}
