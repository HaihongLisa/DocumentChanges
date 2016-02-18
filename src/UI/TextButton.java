package UI;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import IssueExcel.ExcelByDescription;
import IssueExcel.ExcelByKeyWord;
import IssueExcel.ExcelByTimeStamp;
import IssueExcel.ExcelByType;
import IssueLink.JiraByDescription;
import IssueLink.JiraByTimeStamp;
import IssueLink.JiraByType;
import ReleaseNotesLink.WebByDescription;
import ReleaseNotesLink.WebByType;
 
public class TextButton extends JFrame{
	private String changeType;
	private String fileType;
	private String location;
	private String token;
	private HashSet<String> policySet;
	private HashMap<String, HashMap<String, String>> changes;
	public TextButton(String policyStr, String location, String token, String changeType, String fileType) {
		this.changeType = changeType;
		this.fileType = fileType;
		this.token = token;
		this.location = location;
		
		if(policyStr.equals("")){
			JOptionPane.showMessageDialog(null, "Sorry this policy doesn't exist!");
			return;
		}
		
		String[] policies = policyStr.split("\\|");
		policySet = new HashSet<String>();
		for(int i = 0; i < policies.length; i++){
			policySet.add(policies[i]);
		}
		
		setTitle("Filter Information");
		setSize(500, 500);
		setLocation(500, 300);
		setLayout(new FlowLayout());
		
		JTextArea jta = new JTextArea(4,40);
		jta.setText("By Description - Default");
		jta.setEditable(false);
		add(jta);
		
		JLabel version1Label = new JLabel("Version1");
		final JTextField version1Text = new JTextField(10);
		add(version1Label);
		add(version1Text);

		JLabel version2Label = new JLabel("Version2");
		final JTextField version2Text = new JTextField(10);
		add(version2Label);
		add(version2Text);
		
		// part 2
		JTextArea jta2 = new JTextArea(4,40);
		jta2.setText("By Timestamp");
		jta2.setEditable(false);
		add(jta2);
		   
		JLabel timestampLabel = new JLabel("TimeStamp");
		final JTextField timestampText = new JTextField(10);
		add(timestampLabel);
		add(timestampText);
		   		  
		// part 3
		JTextArea jta3 = new JTextArea(4,40);
		jta3.setText("By Type");
		jta3.setEditable(false);
		add(jta3);
		   
		JLabel typeLabel = new JLabel("Type");
		final JTextField typeText = new JTextField(10);
		add(typeLabel);
		add(typeText);
		  
		// part 4
		JTextArea jta4 = new JTextArea(4,40);
		jta4.setText("By Keyword");
		jta4.setEditable(false);
		add(jta4);
		   
		JLabel keywordLabel = new JLabel("Keyword");
		final JTextField keywordText = new JTextField(10);
		add(keywordLabel);
		add(keywordText);
		JButton button = new JButton("Fetch Changes");
		add(button);
		   
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ret = "";
				try {
					ret = changesFilter(version1Text.getText(), version2Text.getText(), timestampText.getText(),
							typeText.getText(),keywordText.getText());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				setJFrame(ret);
			}
		});
		  
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	 }
	 
	 private void setJFrame(String str){
		 JFrame frame = new JFrame ("Changes");
		 frame.setLocation(520, 350);
         frame.getContentPane().add (new MyPanel2(str));
         frame.pack();
         frame.setVisible (true);
	 }
	 
	 
	 public String changesFilter(String version1, String version2, String timeStamp, String type, String keyword) throws Exception{
		 changes = new HashMap<String, HashMap<String, String>>();
		 HashMap<String, HashMap<String, String>> issuesMap = new HashMap<String, HashMap<String, String>>();
		 if(changeType == "Issues" && fileType == "link") {
			 JiraByDescription jiraByDesc = new JiraByDescription(token);
			 issuesMap = jiraByDesc.fetchChangesByDescription(version1, version2);
			 
			 //If type is entered, need merge result
			 if(policySet.contains("byType") && type != "") {
				 JiraByType jiraByType = new JiraByType(token, version1, version2, type);
				 HashMap<String, HashMap<String, String>> typeMap = jiraByType.fetchChangesByType(type);
				 mergeHashMap(issuesMap, typeMap);
			 }
			 
			 if(policySet.contains("byKeyWord") && keyword != "") {
				 throw new IllegalArgumentException("Not support keyword for link issues, please implement the function first");
			 }
			 
			 if(policySet.contains("byTimeStamp") && timeStamp != "") {
				 JiraByTimeStamp jirabyTimeStamp = new JiraByTimeStamp(token, version1, version2, timeStamp);
				 HashMap<String, HashMap<String, String>> timestampMap = jirabyTimeStamp.fetchChangesByTimeStamp(timeStamp);
				 mergeHashMap(issuesMap, timestampMap);
			 }
			 
		 } else if(changeType == "Issues" && fileType == "excel") {
			 ExcelByDescription excelByDesc = new ExcelByDescription(location);
			 issuesMap = excelByDesc.fetchChangesByDescription(version1, version2);
			 			 
			 if(policySet.contains("byType") && !type.equals("")) {
				 ExcelByType excelByType = new ExcelByType(location, version1, version2);
				 HashMap<String, HashMap<String, String>> typeMap = excelByType.fetchChangesByType(type);
				 mergeHashMap(issuesMap, typeMap);
			 }
			 
			 if(policySet.contains("byKeyWord") && !keyword.equals("")) {
				 System.out.println("word :" + "I should not be called");
				 ExcelByKeyWord excelByKeyWord = new ExcelByKeyWord(location, version1, version2);
				 HashMap<String, HashMap<String, String>> wordMap = excelByKeyWord.fetchChangesByKeyWord(keyword);
				 mergeHashMap(issuesMap, wordMap);
			 }
			 
			 if(policySet.contains("byTimeStamp") && !timeStamp.equals("")) {
				 ExcelByTimeStamp excelbyTimeStamp = new ExcelByTimeStamp(location, version1, version2);
				 HashMap<String, HashMap<String, String>> timestampMap = excelbyTimeStamp.fetchChangesByTimeStamp(timeStamp);
				 mergeHashMap(issuesMap, timestampMap);
			 }
		 } else if(changeType == "ReleaseNotes" && fileType == "link") {
			 WebByDescription webReleaseNotes = new WebByDescription(token, version1, version2);
			 issuesMap = webReleaseNotes.fetchChangesByDescription(version1, version2);
			 
			 //If type is entered, need merge result
			 if(policySet.contains("byType") && type != "") {
				 WebByType releaseNotesByType = new WebByType(token, version1, version2);
				 HashMap<String, HashMap<String, String>> typeMap = releaseNotesByType.fetchChangesByType(type);
				 mergeHashMap(issuesMap, typeMap);
			 }
			 
			 if(policySet.contains("byKeyWord") && keyword != "") {
				 throw new IllegalArgumentException("Not support keyword for link issues, please implement the function first");
			 }
			 
			 if(policySet.contains("byTimeStamp") && timeStamp != "") {
				 throw new IllegalArgumentException("Not support keyword for link issues, please implement the function first");
			 }
		 }
		 
		 if(changes.size() == 0) {
			 changes = issuesMap;
		 }
		 
		 StringBuilder ret = new StringBuilder();
		 for(String version : changes.keySet()) {
			 ret.append(version);
			 ret.append("\n");
			 HashMap<String, String> map = changes.get(version);
			 for(String str : map.keySet()) {
				 ret.append(str);
				 ret.append(" : ");
				 ret.append(map.get(str));
				 ret.append("\n");
			 }
		 }
		 return ret.toString();
	 }
	 
	 private void mergeHashMap(HashMap<String, HashMap<String, String>> issuesMap, HashMap<String, HashMap<String, String>> map) {
		 for(String version : issuesMap.keySet()) {	
			 if(map.containsKey(version)) {
				 HashMap<String, String> map1 = issuesMap.get(version);
				 HashMap<String, String> map2 = map.get(version);
				 HashMap<String, String> changeMap;
				 for(String str : map1.keySet()) {
					 if(map2.containsKey(str)) {
						 if(changes.containsKey(version)) {
							 changeMap = changes.get(version);
						 } else {
							 changeMap = new HashMap<String, String>();
						 }
						 changeMap.put(str, map1.get(str));
						 changes.put(version, changeMap);
					 }
				 }
			 }
		 }
	 }
}