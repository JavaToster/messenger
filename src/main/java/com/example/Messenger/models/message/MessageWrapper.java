package com.example.Messenger.models.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.util.enums.MessageStatus;
import com.example.Messenger.util.enums.MessageType;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Message_wrapper")
@Inheritance(strategy = InheritanceType.JOINED)
public class MessageWrapper {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(name = "content")
    protected String content;
    @Enumerated(value = EnumType.STRING)
    protected MessageType type;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date sendingTime;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    protected MessengerUser owner;
    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    protected Chat chat;
    @Enumerated(value = EnumType.ORDINAL)
    protected MessageStatus hasBeenRead;

    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

    public MessengerUser getOwner() {
        return owner;
    }

    public void setOwner(MessengerUser owner) {
        this.owner = owner;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public MessageStatus getHasBeenRead() {
        return hasBeenRead;
    }

    public void setHasBeenRead(MessageStatus hasBeenRead) {
        this.hasBeenRead = hasBeenRead;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public boolean contentIsUrl(){
        if(this.content.length() <8){
            return false;
        }

        String protocol = content.substring(0, 8);

        if (protocol.indexOf("http://") != -1 || protocol.indexOf("https://") != -1) {
            return true;
        }
        return false;
    }

    public String typeToString() {
        return this.type.name().toLowerCase();
    }

    public String strings(){
        return "MessageWrapper{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", sendingTime=" + sendingTime +
                ", owner=" + owner +
                ", chat=" + chat +
                ", hasBeenRead=" + hasBeenRead +
                '}';
    }

    public String getMessageSendingTime() {
        return addZeroToTime(this.sendingTime.getHours())+":"+addZeroToTime(this.sendingTime.getMinutes());
    }

    public String getMessageContent(){
        return this.content;
    }

    private String addZeroToTime(int time){
        return time < 10 ? "0"+time : String.valueOf(time);
    }
}