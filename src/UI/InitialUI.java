package UI;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

public class InitialUI extends JFrame{
    private static String changeType;
    private static String fileType;

    public InitialUI() throws Exception
    {
        super("Enter infomation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 200);
        setLocation(500, 300);
        setLayout(new FlowLayout());
        
        // part 1
        JLabel linkLabel = new JLabel("Enter Link");
		final JTextField linkText = new JTextField(10);
		add(linkLabel);
		add(linkText);
		
		JLabel projectLabel = new JLabel("Project Name");
		final JTextField projectText = new JTextField(10);
		add(projectLabel);
		add(projectText);
        
        JButton btn = new JButton("Confirm");
        
        
        //linkText.getText();
        //projectText.getText();
        
        btn.addActionListener(new ActionListener() {
		    
 		   @Override
 		   public void actionPerformed(ActionEvent e) {
 			  String cmd = e.getActionCommand();
 			  String policyStr;
	            if(cmd.equals("Open"))
	            {
	                dispose();
	                try {
						policyStr = getPolicy(linkText.getText());
						String location = linkText.getText();
						String token = projectText.getText();
						new TextButton(policyStr, location, token, changeType, fileType);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                
	            }
 		   }
 		  });
        
        btn.setActionCommand("Open");
        add(btn);
    }
    
    public String getPolicy(String str) throws Exception{
    	changeType = getChangeType(str);
    	fileType = getFileType(str);
    	// call function to get input
    	String retStr = readXMLFile(fileType, changeType);
    	
    	/*
    	String retStr = "";
    	
    	if(fileType == "link"){
    		switch(input){
	    		case "Issues":
	    			retStr = "byDescrption|byTimeStamp|byType|byKeyWord";
	    		case "ReleaseNotes":
	    			retStr = "byDescrption|byTimeStamp|byType";
	    		case "News":
	    			retStr = "byDescrption|byTimeStamp|byType";
    		}
    	}
    	else if (fileType == "excel"){
    		switch(input){
	    		case "ReleaseNotes":
	    			retStr = "byDescrption";
			}
    	}
    	*/
    	
    	return retStr;
    }
    
    
    public String getChangeType(String str){
    	str = str.toLowerCase();
    	if(str.contains("jira".toLowerCase())) {
    		return "Issues";
    	}
    	return "ReleaseNotes";
    }
    
    public String getFileType(String str) {
    	if(str.contains("http")) {
    		return "link";
    	} else if(str.contains("xls")) {
    		return "excel";
    	} else {
    		return "";
    	}
    }
    
	public String readXMLFile(String fileType, String changeType) throws Exception {
		StringBuilder returnStr = new StringBuilder();
		try {
			
			File xmlFile = new File("classMapping.xml");
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbfactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("changes_type");
			for(int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				Element element = (Element)node;
				if(element.getAttribute("value").equals(changeType)){
					NodeList sublist = element.getElementsByTagName("file_type");
					for(int j = 0; j < sublist.getLength(); j++) {
						Node subnode = sublist.item(j);
						Element subelement = (Element)subnode;
						if(subelement.getAttribute("value").equals(fileType)){
							NodeList childlist = subelement.getChildNodes();
							for(int k = 0; k < childlist.getLength(); k++){
								Node childnode = childlist.item(k);
								if(childnode.getNodeName().equals("policy")){
									returnStr.append(childnode.getTextContent() + "|");
								}
								
							}
						}
					}
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(returnStr.length() != 0){
			return returnStr.toString().substring(0, returnStr.length() - 1);
		}
		return returnStr.toString();
	}

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run()
            {
                try {
					new InitialUI().setVisible(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }
}