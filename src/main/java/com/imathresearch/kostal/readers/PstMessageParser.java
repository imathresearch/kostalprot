package com.imathresearch.kostal.readers;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import com.pff.PSTAttachment;
import com.pff.PSTException;
import com.pff.PSTMessage;

public class PstMessageParser {

    private static PSTMessage message;
    private static DecimalFormat twoDForm = new DecimalFormat("#.##");
    
    String internetMessageId;
    String subject;
    Date clientSubmitTime;
    String displayName;
    String displayTo;
    String displayCC;
    String inReplyToId;
    String body;
    Long attachmentSize;
    Long messageSize;
    
    
    public PstMessageParser() {}
    
    
    public Map<String, Object> parse() throws PSTException, IOException {
            int num = message.getNumberOfAttachments();
            long sizeAtt = 0;
            for(int i =0; i<num; i++) {
                PSTAttachment att = message.getAttachment(i);
                sizeAtt = sizeAtt + att.getFilesize();
            }
            
            setInternetMessageId(message.getInternetMessageId());
            setSubject(message.getSubject());
            setClientSubmitTime(message.getClientSubmitTime());
            
            setDisplayName(message.getDisplayName());
            setDisplayTo(message.getDisplayTo());
            setDisplayCC(message.getDisplayCC());
            setInReplyToId(message.getInReplyToId());

            setBody(message.getBody());
            setAttachmentSize(sizeAtt);
            setMessageSize(message.getMessageSize());
            
            Map<String, Object> m = mapped();
            toJson(m);
            return m;
    }

    
    public Map<String, Object> mapped() {
        Map<String, Object> mappedMessage = new HashMap<String, Object>();
        
        mappedMessage.put("internetMessageId", internetMessageId);
        mappedMessage.put("subject", subject);
        mappedMessage.put("clientSubmitTime",clientSubmitTime);
        mappedMessage.put("displayName", displayName);
        mappedMessage.put("displayTo",displayTo);
        mappedMessage.put("displayCC", displayCC);
        mappedMessage.put("inReplyToId", inReplyToId);
        mappedMessage.put("body", body);
        mappedMessage.put("attachmentSize", attachmentSize);
        mappedMessage.put("messageSize", messageSize);
        
        return mappedMessage;
    }
    
    private String toJson(Map<String, Object> mappedMessage) {
        JSONObject jsonText = new JSONObject(mappedMessage);
        System.out.print(jsonText);
        return jsonText.toString();
    }
    
    public PSTMessage getMessage() {
        return message;
    }

    public void setMessage(PSTMessage message) {
        PstMessageParser.message = message;
    }

    public String getInternetMessageId() {
        return internetMessageId;
    }

    public String getSubject() {
        return subject;
    }


    public Date getClientSubmitTime() {
        return clientSubmitTime;
    }


    public String getDisplayName() {
        return displayName;
    }


    public String getDisplayTo() {
        return displayTo;
    }


    public String getDisplayCC() {
        return displayCC;
    }


    public String getInReplyToId() {
        return inReplyToId;
    }


    public long getAttachmentSize() {
        return attachmentSize;
    }


    public long getMessageSize() {
        return messageSize;
    }


    public void setInternetMessageId(String internetMessageId) {
        this.internetMessageId = internetMessageId;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public void setClientSubmitTime(Date clientSubmitTime) {
        this.clientSubmitTime = clientSubmitTime;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public void setDisplayTo(String displayTo) {
        this.displayTo = displayTo;
    }


    public void setDisplayCC(String displayCC) {
        this.displayCC = displayCC;
    }


    public void setInReplyToId(String inReplyToId) {
        this.inReplyToId = inReplyToId;
    }


    public void setAttachmentSize(long attachmentSize) {
        this.attachmentSize = attachmentSize;
    }


    public void setMessageSize(long messageSize) {
        this.messageSize = messageSize;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    
}
