package makeable.poc_firebase_chatmessaging;

import java.util.Date;

/**
 * Created by frederik on 23/11/2017.
 * This class holds the chatmessages and the relevant data.
 *
 * It consists of the three types of data we wish to safe as well as getters and setters. It is the object we give to the database.
 *
 * In the event of a login, as discussed in the attached text document, a uid should be added.
 */

public class Chatmessage {
    private String messageUser;
    private String messageText;
    private long messageTime;

    public Chatmessage(String messageText, String messageUser){
        this.messageText = messageText;
        this.messageUser = messageUser;

        messageTime = new Date().getTime();
    }

    public Chatmessage(){

    }

    public String getMessageUser(){
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageText(){
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Long getMessageTime(){
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
