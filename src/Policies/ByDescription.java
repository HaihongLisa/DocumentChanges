package Policies;

import java.util.HashMap;

public interface ByDescription {
	public HashMap<String, HashMap<String, String>> fetchChangesByDescription(String verison1, String version2) throws Exception;
}
