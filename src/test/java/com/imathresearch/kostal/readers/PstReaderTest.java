package com.imathresearch.kostal.readers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.pff.PSTFile;
import com.pff.PSTFolder;

public class PstReaderTest {
    

    private static PstReader reader;
    
    @Before
    public void initLoad() {
        loadPstFileTest();
    }

    public void loadPstFileTest() {
        try {
            reader = new PstReader("Obra.pst");
            
            assertTrue(reader.getFile().exists());
            assertNotNull(reader.getPstFile());
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void getFolderContentTest() {
        PSTFile pstFile = reader.getPstFile();
        assertNotNull(pstFile);
        
        try {
            pstFile.getRootFolder().getContentCount();
            PSTFolder root = pstFile.getRootFolder();
            reader.getFolderContent(root);
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
}