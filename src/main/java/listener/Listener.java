package listener;

import gobang.Game;
import manager.GameManager;
import main.GoBang;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;



import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Listener extends ListenerAdapter {
    GameManager gameManager = GoBang.gameManager;

    Map<Member, Member> map = new HashMap<>();

    




    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);


        if (event.getAuthor().isBot() || event.getAuthor().isSystem() || !gameManager.player.contains(event.getMember())) {
            return;
        }



        if(event.getMember().getId().equals("598163613106307092") && event.getMessage().getContentDisplay().startsWith("高高手")){

            gameManager.player.remove(event.getMessage().getMentionedMembers().get(0));

            event.getMessage().delete().queue();
        }
        try {
            if (event.getChannel() == gameManager.playerGameMap.get(event.getMember()).channel) {

                try {
                    int a = (int) event.getMessage().getContentDisplay().split("", 2)[0].getBytes(StandardCharsets.UTF_8)[0] - 64;
                    int b = Integer.parseInt(event.getMessage().getContentDisplay().split("", 2)[1]);
                } catch (NumberFormatException e) {
                    event.getMessage().delete().queueAfter(6, TimeUnit.SECONDS);
                    return;
                }

                int x = (int) event.getMessage().getContentDisplay().split("", 2)[0].getBytes(StandardCharsets.UTF_8)[0] - 64;
                int y = Integer.parseInt(event.getMessage().getContentDisplay().split("", 2)[1]);

                if (!(gameManager.playerGameMap.get(event.getMember()).peopleColor
                        .equals(getKeyByValue(gameManager.playerGameMap.get(event.getMember()).pieceColorMap, event.getMember())))) {

                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage("現在不是你落子").queue(r -> r.delete().queueAfter(3, TimeUnit.SECONDS));
                    return;
                }

                if (!(rangeInDefined(x, 1, 15)) || !(rangeInDefined(y, 1, 15))) {

                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage("棋盤上沒有這個位置，請重新落子").queue(r -> r.delete().queueAfter(3, TimeUnit.SECONDS));

                    return;
                }

                event.getMessage().delete().queueAfter(3, TimeUnit.SECONDS);
                gameManager.playerGameMap.get(event.getMember()).chess(event.getMessage().getContentDisplay());
            }
        }catch (NullPointerException ignored){}
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);

        switch (event.getName()) {
            case "set":
                if(!event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                    event.getChannel().sendMessage("只有管理員能設定類別").queue(r -> r.delete().queueAfter(5,TimeUnit.SECONDS));
                    return;
                }
                if(event.getJDA().getCategoryById(event.getOption("類別id").getAsString()) == null){
                    event.getChannel().sendMessage("此類別不存在").queue();
                    return;
                }

                gameManager.categoryMap.put(event.getGuild().getId(), event.getOption("類別id").getAsString());

                try {
                    FileWriter fileWriter = new FileWriter("category.json");
                    JSONObject ob = new JSONObject(gameManager.categoryMap);
                    ob.put(event.getGuild().getId(),event.getOption("類別id").getAsString());
                    System.out.println(ob.toJSONString());
                    fileWriter.write(ob.toJSONString());
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                event.getInteraction().reply("成功將類別: **" + event.getJDA().getCategoryById(event.getOption("類別id").getAsString()).getName() + "** 設為戰場").queue();
                break;
            case "play":

                if (gameManager.player.contains(event.getMember())) {
                    event.reply("請先將目前對局或目前對戰請求結束").queue();
                }

                Member player1 = event.getMember();

                Member player2 = event.getOption("play").getAsMember();

                map.put(player2, player1);
                gameManager.player.add(player1);
                gameManager.player.add(player2);
                event.getInteraction().reply(player2.getAsMention() + "是否同意他的對戰請求?").addActionRow(
                        Button.primary("yes", "是"),
                        Button.primary("no", "否"),
                        Button.primary("cancel", "取消")).queue();
                break;

        }


    }

    @Override
    public void onGuildJoin(GuildJoinEvent event){
        super.onGuildJoin(event);
        Guild guild = event.getGuild();
        guild.upsertCommand("set", "設定五子棋戰場出現的類別 /set ID")
                .addOption(OptionType.STRING, "類別id", "輸入一段數字", true).queue();

        guild.upsertCommand("play", "跟一名成員展開遊戲! /play @Tag某人")
                .addOption(OptionType.MENTIONABLE, "play", "@tag他", true).queue();

    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        switch (event.getComponentId()) {
            case "yes":
                if (map.containsValue(event.getMember())) {
                    event.getChannel().sendMessage(event.getMember().getAsMention() + "請耐心等待對手回應><").queue(
                            r -> r.delete().queueAfter(3, TimeUnit.SECONDS)
                    );
                    break;
                } else if (!gameManager.player.contains(event.getMember())) {
                    return;
                }
                try {
                    event.getInteraction().reply(event.getMember().getAsMention() + " 接受了對戰").queue();
                    event.getInteraction().getMessage().delete().queue();
                    Objects.requireNonNull(event.getJDA().getCategoryById(gameManager.categoryMap.get(event.getGuild().getId())))
                            .createTextChannel(map.get(event.getMember()).getEffectiveName() + " vs " + event.getMember().getEffectiveName())
                            .addPermissionOverride(event.getGuild().getPublicRole(), EnumSet.of(Permission.VIEW_CHANNEL), EnumSet.of(Permission.MESSAGE_SEND))
                            .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                            .addPermissionOverride(map.get(event.getMember()), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                            .queue(r -> {
                                gameManager.map.put(r.getId(), new Game(map.get(event.getMember()), event.getMember(), r));
                                map.remove(event.getMember());
                            });
                } catch (IllegalArgumentException e) {
                    event.getChannel().sendMessage("尚未設定戰場類別").queue();
                    gameManager.player.remove(event.getMember());
                    gameManager.player.remove(map.get(event.getMember()));
                    event.getInteraction().getMessage().delete().queue();
                }
                break;
            case "no":
                if (map.containsValue(event.getMember())) {
                    event.getChannel().sendMessage(event.getMember().getAsMention() + "請耐心等待對手回應><").queue(
                            r -> r.delete().queueAfter(3, TimeUnit.SECONDS)
                    );
                    break;
                } else if (!gameManager.player.contains(event.getMember())) {
                    return;
                }
                gameManager.player.remove(event.getMember());
                gameManager.player.remove(map.get(event.getMember()));
                event.getInteraction().reply(event.getMember().getAsMention() + " 拒絕了對戰").queue();

                event.getInteraction().getMessage().delete().queue();
                map.remove(event.getMember());
                break;
            case "cancel":
                if (!gameManager.player.contains(event.getMember())) {
                    return;
                }
                gameManager.player.remove(event.getMember());
                gameManager.player.remove(map.get(event.getMember()));
                event.reply(event.getMember().getAsMention() + "取消了對戰").queue();
                event.getInteraction().getMessage().delete().queue();
                map.remove(event.getMember());
                break;
            case "end":
                if (gameManager.map.get(event.getChannel().getId()) == gameManager.playerGameMap.get(event.getMember())) {
                    gameManager.map.get(event.getChannel().getId()).end();
                }

                break;
            case "confirm":
                if (gameManager.map.get(event.getChannel().getId()) == gameManager.playerGameMap.get(event.getMember())) {
                    event.getChannel().delete().queue();
                }

                break;
        }

    }

    public <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean rangeInDefined(int current, int min, int max) {
        return Math.max(min, current) == Math.min(current, max);
    }


}
