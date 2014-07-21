package com.imathresearch.kostal.readers;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pff.PSTAttachment;
import com.pff.PSTException;
import com.pff.PSTMessage;

public class PstMessageParser {

    private static PSTMessage message;
    private static DecimalFormat twoDForm = new DecimalFormat("#.##");
    public static String emptyField = "EMPTY";
    
    String internetMessageId;
    String subject;
    Date clientSubmitTime;
    
    String displayName;
    String displayTo;
    String displayCC;
    String displayBCC;
    
    String emailAddress;
    String originalDisplayTo;
    String originalDisplayCc;
    String originalDisplayBcc;
    
    String body;
    Long attachmentSize;
    Long messageSize;
    
    String inReplyToId;
    String conversationTopic;
    String transportMessageHeaders;
    String threadIndex;
    String threadTopic;
    
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
            setDisplayBCC(message.getDisplayBCC());
            setInReplyToId(message.getInReplyToId());
            
            setEmailAddress(message.getEmailAddress());
            setOriginalDisplayTo(message.getOriginalDisplayTo());
            setOriginalDisplayCc(message.getOriginalDisplayCc());
            setOriginalDisplayBcc(message.getOriginalDisplayBcc());

            setBody(message.getBody());
            setAttachmentSize(sizeAtt);
            setMessageSize(message.getMessageSize());

            setConversationTopic(message.getConversationTopic());
            setTransportMessageHeaders(message.getTransportMessageHeaders());
            if (transportMessageHeaders == null || "".equals(transportMessageHeaders)) {
                threadIndex = emptyField;
                threadTopic = emptyField;
            } else {
                threadIndex = extractHeaderValue("Thread-Index", transportMessageHeaders);
                threadTopic = extractHeaderValue("Thread-Topic", transportMessageHeaders);
            }
            
            Map<String, Object> m = toMap();
            return m;
    }

    
    private Map<String, Object> toMap() {
        Map<String, Object> mappedMessage = new HashMap<String, Object>();
        
        mappedMessage.put("internetMessageId", internetMessageId);
        mappedMessage.put("subject", subject);
        mappedMessage.put("clientSubmitTime",clientSubmitTime);

        mappedMessage.put("displayName", displayName);
        mappedMessage.put("displayTo",displayTo);
        mappedMessage.put("displayCC", displayCC);
        mappedMessage.put("displayBCC", displayBCC);
        
        mappedMessage.put("emailAddress", emailAddress);
        mappedMessage.put("originalDisplayTo", originalDisplayTo);
        mappedMessage.put("originalDisplayCc", originalDisplayCc);
        mappedMessage.put("originalDisplayBcc", originalDisplayBcc);
        
        mappedMessage.put("body", body);
        mappedMessage.put("attachmentSize", attachmentSize);
        mappedMessage.put("messageSize", messageSize);
        
        mappedMessage.put("inReplyToId", inReplyToId);
        mappedMessage.put("conversationTopic", conversationTopic);
        mappedMessage.put("transportMessageHeaders", transportMessageHeaders);
        mappedMessage.put("threadIndex", threadIndex);
        mappedMessage.put("threadTopic", threadTopic);
        
        return mappedMessage;
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

    public String getConversationTopic() {
        return conversationTopic;
    }

    public void setConversationTopic(String conversationTopic) {
        this.conversationTopic = conversationTopic;
    }

    public String getTransportMessageHeaders() {
        return transportMessageHeaders;
    }

    public void setTransportMessageHeaders(String transportMessageHeaders) {
        this.transportMessageHeaders = transportMessageHeaders;
    }
    
    public String getThreadIndex() {
        return threadIndex;
    }

    public String getThreadTopic() {
        return threadTopic;
    }

    public void setThreadIndex(String threadIndex) {
        this.threadIndex = threadIndex;
    }

    public void setThreadTopic(String threadTopic) {
        this.threadTopic = threadTopic;
    }
    
    private String extractHeaderValue(String header, String headers) {
        Pattern p = Pattern.compile("\\n" + header + ": ([^\\n\\r]*)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(headers);
        if (m.find()) {
            return m.group(1);
        }
        return emptyField;
    }


    public String getDisplayBCC() {
        return displayBCC;
    }


    public String getEmailAddress() {
        return emailAddress;
    }


    public String getOriginalDisplayBcc() {
        return originalDisplayBcc;
    }


    public String getOriginalDisplayCc() {
        return originalDisplayCc;
    }


    public String getOriginalDisplayTo() {
        return originalDisplayTo;
    }


    public void setMessageSize(Long messageSize) {
        this.messageSize = messageSize;
    }


    public void setDisplayBCC(String displayBCC) {
        this.displayBCC = displayBCC;
    }


    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    public void setOriginalDisplayBcc(String originalDisplayBcc) {
        this.originalDisplayBcc = originalDisplayBcc;
    }


    public void setOriginalDisplayCc(String originalDisplayCc) {
        this.originalDisplayCc = originalDisplayCc;
    }


    public void setOriginalDisplayTo(String originalDisplayTo) {
        this.originalDisplayTo = originalDisplayTo;
    }

}
