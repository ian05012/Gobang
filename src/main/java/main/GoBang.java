package main;

import listener.Listener;


import manager.GameManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.json.simple.parser.ParseException;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.EnumSet;

public class GoBang {
    public static JDA jda;
    public static GameManager gameManager;

    GoBang() {
    }

    public static void main(String[] args) throws LoginException, InterruptedException {


        gameManager = new GameManager();

        File file = new File("Game");
        if (!file.exists()) {
            if (!(file.mkdir())) {
                System.out.println("檔案創建失敗。");
            }
        }



        JDA jda;
        jda = JDABuilder.create(token,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.DIRECT_MESSAGES).disableCache(EnumSet.of(
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOTE)).enableCache(CacheFlag.VOICE_STATE, CacheFlag.MEMBER_OVERRIDES)
                .addEventListeners(new Listener())
                .setActivity(Activity.playing("要不要怕五子棋")).build().awaitReady();




        try {
            gameManager.load();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        for (Guild guild : jda.getGuilds()) {

            guild.upsertCommand("set", "設定五子棋戰場出現的類別 /set ID")
                    .addOption(OptionType.STRING, "類別id", "輸入一段數字", true).queue();

            guild.upsertCommand("play", "跟一名成員展開遊戲! /play @Tag某人")
                    .addOption(OptionType.MENTIONABLE, "play", "@tag他", true).queue();

        }








    }
}
