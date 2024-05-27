package com.example.Messenger.dto.bot.response.message;

import com.example.Messenger.util.abstractClasses.InfoOfMessage;

import java.util.LinkedList;
import java.util.List;

public class InfoByImageMessageDTO extends InfoOfMessage {
    private List<Byte> bytesOfImage;

    public InfoByImageMessageDTO(int chatId, byte[] bytesOfImage) {
        this.chatId = chatId;
        this.bytesOfImage = convertToBytesList(bytesOfImage);
        this.type = "image";
    }

    private List<Byte> convertToBytesList(byte[] bytesOfImage){
        List<Byte> bytes = new LinkedList<>();
        for(byte byte1: bytesOfImage){
            bytes.add(byte1);
        }
        return bytes;
    }

    public List<Byte> getBytesOfImage() {
        return bytesOfImage;
    }

    public void setBytesOfImage(List<Byte> bytesOfImage) {
        this.bytesOfImage = bytesOfImage;
    }
}
