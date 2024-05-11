package com.preorder.infra.discord;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DiscordBot extends ListenerAdapter {

    private Map<String,String> channelHashMap= new HashMap<>();
    private JDA jda;
    @Value("${discord.bot.token}")
    private String discordBotToken;

    @PostConstruct
    public void init() throws IOException {
         jda = JDABuilder.createDefault(discordBotToken)
                .setActivity(Activity.playing("서버 실행중"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(this)
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<TextChannel> textChannels = jda.getTextChannels();
        //In-Memory로 들고 있는게 낫지 않은가?
        for(TextChannel textChannel: textChannels){
            log.info("ID :" +  textChannel.getId()+"NAME: "+ textChannel.getName() );
            channelHashMap.put(textChannel.getName(),textChannel.getId());
        }

    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);
        User user = event.getAuthor();
        TextChannel textChannel = event.getChannel().asTextChannel();
        Message message = event.getMessage();

        String plainMessage = message.getContentDisplay();
        log.info("get Message : " + message.getContentDisplay());
        if (user.isBot()) {
            return;
        } else {
            if (message.getContentDisplay().startsWith("!")) {
                plainMessage = plainMessage.replace("!", "");
                String returnMessage = makeReturnMessage(event, plainMessage);
                textChannel.sendMessage(returnMessage).queue();
            }
        }
    }

    public void sendMessage(String channelName,String message){

        final String id = channelHashMap.get(channelName);
        final TextChannel textChannel = jda.getTextChannelById(id);
        if(textChannel!=null){
            textChannel.sendMessage(message).queue();
        }
        else{
            log.error("유효하지 않은 채널이름 : {}",channelName);
        }

    }


    private String makeReturnMessage(MessageReceivedEvent event, String message) {

        /*
        * 주문정보 Return 하는 것 만들기
        *
        * */
        //여기서 주문정보 get 하는것 정도는 충분히 가능할거 같다.
        User user = event.getAuthor();
        String returnMessage = "";
        switch (message) {
            case "이름":
                returnMessage = user.getName();
                break;
            case "태그":
                returnMessage = user.getAsTag();
                break;
            case "테스트":
                returnMessage = user.getAsMention();
                break;
            default:
                returnMessage = "없는 명령어 입니다 :(";
                break;
        }
        return returnMessage;
    }
}
