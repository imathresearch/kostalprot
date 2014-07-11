package com.imathresearch.kostal.readers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;

import com.pff.PSTAttachment;
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
    
     
    public void getFolderContent(PSTFolder folder) throws PSTException,
            java.io.IOException {
        
        System.out.println(folder.getDisplayName());
        getMessages(folder);
        System.out.println("--------------------------------------");
        
        // go through the folders...
        if (folder.hasSubfolders()) {
            Vector<PSTFolder> childFolders = folder.getSubFolders();
            for (PSTFolder childFolder : childFolders) {
                getFolderContent(childFolder);
            }
        }
    }
    
    public void getMessages(PSTFolder folder) throws PSTException, IOException {
     // and now the emails for this folder
        int numMessages = folder.getContentCount();
        System.out.println(">>> NUMBER OF MESS: " + numMessages);
        for (int i = 0; i < numMessages; i++) {
            
            PSTObject emailObj = folder.getNextChild();
            PSTMessage email = (PSTMessage)emailObj;
            PstMessageParser parser = new PstMessageParser();
            if (email != null) {
                parser.setMessage(email);
                parser.parse();
            }
            email = (PSTMessage)folder.getNextChild();
        }
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

    
    protected File getFile() {
        return file;
    }

    protected void setFile(File file) {
        PstReader.file = file;
    }
}