package com.imathresearch.kostal.readers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pff.PSTException;
import com.pff.PSTFile;
//import com.pff.PSTFolder;
import com.pff.PSTFolder;
import com.pff.PSTMessage;
import com.pff.PSTObject;

public class PstReader {
    
    private static String basePath =  System.getProperty("RESOURCE_PATH", "target/classes");
    private static String pathName = basePath + "/PSTs";
    private static File file;
    private PSTFile pstFile;

    public PstReader(String pstName) throws FileNotFoundException, PSTException, IOException {
        file = new File(pathName + "/" + pstName);
        pstFile = new PSTFile(file);
    }
    
     
    public Map<String, List<Map<String,Object>>> getFolderContent(PSTFolder folder) throws PSTException,
            java.io.IOException {
        
        if ("".equals(folder.getDisplayName())) {
            folder.getNextChild();
        }
        Map<String, List<Map<String,Object>>> contentMap = new HashMap<String, List<Map<String,Object>>>();
        contentMap = getMessages(folder);
        
        // go through the folders...
        if (folder.hasSubfolders()) {
            Vector<PSTFolder> childFolders = folder.getSubFolders();
            for (PSTFolder childFolder : childFolders) {
                contentMap.putAll(getFolderContent(childFolder));
            }
        }
        return contentMap;
    }
    
    public Map<String, List<Map<String, Object>>> getMessages(PSTFolder folder) throws PSTException, IOException {
     // and now the emails for this folder
        int numMessages = folder.getContentCount();
        PstMessageParser parser = new PstMessageParser();
        Map<String, List<Map<String,Object>>> content = new HashMap<String, List<Map<String,Object>>>();
        List<Map<String,Object>> emailList = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < numMessages; i++) {
            
            PSTObject emailObj = folder.getNextChild();
            PSTMessage email = (PSTMessage)emailObj;
              
            if (email != null) {
                parser.setMessage(email);
                emailList.add(parser.parse());
            }
            email = (PSTMessage)folder.getNextChild();
        }
        content.put(folder.getDisplayName(), emailList);
        return content;
    }

    /*
     * GETTERS AND SETTERS
     */
    
    public PSTFile getPstFile() {
        return pstFile;
    }

    protected void setPstFile(PSTFile pstFile) {
        this.pstFile = pstFile;
    }

    
    public File getFile() {
        return file;
    }

    protected void setFile(File file) {
        PstReader.file = file;
    }
}