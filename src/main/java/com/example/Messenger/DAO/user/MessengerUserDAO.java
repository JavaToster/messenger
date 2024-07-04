package com.example.Messenger.DAO.user;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class MessengerUserDAO {
    public MessengerUser getInterlocutorFromChat(Chat chat, String username){
        List<ChatMember> members = chat.getMembers();
        for(ChatMember member: members){
            if(!member.getUsernameOfUser().equals(username)){
                return member.getUser();
            }
        }
        return null;
    }

    public String getLastOnlineTimeAsString(User user){
        Calendar calendarOfUser = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();

        Date userLastOnlineTime = user.getLastOnlineTime();
        Date timeNow = new Date();

        calendarOfUser.setTime(userLastOnlineTime);
        calendarNow.setTime(timeNow);

        if(userLastOnlineTime == null){
            return "Был(а) недавно";
        }


        if(isUserOnline(calendarNow, calendarOfUser)){
            return "в cети";
        }

        int yearNow = calendarNow.get(Calendar.YEAR);
        int monthNow = calendarNow.get(Calendar.MONTH);
        int dayNow = calendarNow.get(Calendar.DAY_OF_MONTH);
        int yearOfUser = calendarOfUser.get(Calendar.YEAR);
        int monthOfUser = calendarOfUser.get(Calendar.MONTH);
        int dayOfUser = calendarOfUser.get(Calendar.DAY_OF_MONTH);

        if(yearNow > yearOfUser){
            return "Был(а) в сети "+(yearNow-yearOfUser)+" "+getNameOfYear(yearNow-yearOfUser)+" назад";
        }else{
            if(monthNow > monthOfUser){
                return "Был(а) в сети "+(monthNow - monthOfUser)+" "+getNameOfMonth(monthNow-monthOfUser)+" назад";
            } else if (dayNow > dayOfUser) {
                if(dayNow-dayOfUser == 1) {
                    return "Был(а) в сети вчера";
                }else{
                    return "Был(а) в сети "+(dayNow-dayOfUser)+" "+getNameOfDay(dayNow-dayOfUser)+" назад";
                }
            }else{
                return "Был(а) в сети в "+addZeroToTime(calendarOfUser.getTime().getHours())+":"+addZeroToTime(calendarOfUser.getTime().getMinutes());
            }
        }
    }

    private String getNameOfYear(int differenceOfYear){
        if(differenceOfYear == 1){
            return "год";
        }else{
            return differenceOfYear<5 ? "года" : "лет";
        }
    }

    private String getNameOfMonth(int differenceOfMonth){
        return differenceOfMonth == 1 ? "месяц" : "месяцев";
    }
    private String getNameOfDay(int differenceOfDay){
        return differenceOfDay < 5 ? "дня" : "дней";
    }
    private String addZeroToTime(int time){
        return time<10? "0"+time: ""+time;
    }
    private boolean isUserOnline(Calendar timeNow, Calendar lastOnlineTimeOfUser){
        if(
                timeNow.get(Calendar.YEAR) == lastOnlineTimeOfUser.get(Calendar.YEAR) &&
                        timeNow.get(Calendar.MONTH) == lastOnlineTimeOfUser.get(Calendar.MONTH) &&
                        timeNow.get(Calendar.DAY_OF_MONTH) == lastOnlineTimeOfUser.get(Calendar.DAY_OF_MONTH) &&
                        timeNow.get(Calendar.HOUR) == lastOnlineTimeOfUser.get(Calendar.HOUR) &&
                        timeNow.get(Calendar.MINUTE) == lastOnlineTimeOfUser.get(Calendar.MINUTE)
        )
        {
            return true;
        }
        return false;
    }
}
