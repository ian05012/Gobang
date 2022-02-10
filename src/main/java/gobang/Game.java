package gobang;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import main.GoBang;
import manager.GameManager;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game {


    public TextChannel channel;
    public Map<String, Member> pieceColorMap = new HashMap<>();
    public String peopleColor = "黑";

    private Message message;
    private final GUI gui;
    private final int BOARD_SIZE = 15;
    private final String[][] board = new String[BOARD_SIZE][BOARD_SIZE];
    private final EmbedBuilder builder = new EmbedBuilder();
    private final Cloudinary cloudinary;

    Member player1;
    Member player2;

    public Game(Member player1, Member player2, TextChannel textChannel) {

        this.player1 = player1;
        this.player2 = player2;
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloud_name,
                "api_key", api_key,
                "api_secret", api_secret,
                "upload_preset", upload_preset
        ));

        gui = new GUI();
        pieceColorMap.put("黑", player1);
        pieceColorMap.put("白", player2);

        GameManager gm = GoBang.gameManager;
        gm.playerGameMap.put(player1, this);
        gm.playerGameMap.put(player2, this);

        channel = textChannel;

        gui.paint(channel.getId());

        builder.setTitle(player1.getEffectiveName() + "與" + player2.getEffectiveName() + "的對局");//.setImage("attachment://checkerboard.png");

        builder.setImage(getUrl());

        builder.setDescription(pieceColorMap.get("黑").getAsMention() + " 為黑棋\n" +
                pieceColorMap.get("白").getAsMention() + " 為白棋\n\n" +
                "落子請先輸入橫向再輸入縱向\n範例: A7、C14、O8\n\n遊戲中可以直接對話，但是6秒後將被刪除");

        builder.setFooter("輪到 " + pieceColorMap.get(peopleColor).getEffectiveName());
        builder.setColor(Color.BLACK);
        channel.sendMessage(player1.getAsMention() + player2.getAsMention()).queue(r -> r.delete().queueAfter(1, TimeUnit.SECONDS));


        channel.sendMessageEmbeds(builder.build()).setActionRow(

                        Button.primary("end", "終止對戰"))

                .queue(r -> this.message = r);


        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = "0";
            }
        }

    }

    public void chess(String position) {

        String[] posStrArr = position.split("", 2);
        int x = posStrArr[0].getBytes(StandardCharsets.UTF_8)[0] - 64;
        int y = Integer.parseInt(posStrArr[1]);


        if (!isOk(x, y)) {
            channel.sendMessage("這邊已經有棋子了,請重新落子").queue(r -> r.delete().queueAfter(3, TimeUnit.SECONDS));
            return;
        }

        board[x - 1][y - 1] = pieceColor(peopleColor);

        gui.paintPiece(x - 1, y - 1, peopleColor, channel.getId());

        peopleColor = getWhoPiece(peopleColor);
        builder.setFooter("輪到 " + pieceColorMap.get(peopleColor).getEffectiveName());
        builder.setColor(getColor(peopleColor));
        builder.setImage(getUrl());

        message.editMessageEmbeds(builder.build()).queue();

        peopleColor = getWhoPiece(peopleColor);
        if (isWin(x - 1, y - 1, peopleColor) != null) {

            channel.sendMessage(pieceColorMap.get(peopleColor).getAsMention() + " 獲勝").queue();
            for (String str : isWin(x - 1, y - 1, peopleColor)) {
                gui.paintPiece(Integer.parseInt(str.split(",")[0]), Integer.parseInt(str.split(",")[1]), "紅", channel.getId());
            }
            builder.setFooter("輪到 " + pieceColorMap.get(peopleColor).getEffectiveName());
            builder.setColor(getColor(peopleColor));
            builder.setImage(getUrl());

            message.editMessageEmbeds(builder.build()).queue();
            end();
            return;
        }

        peopleColor = getWhoPiece(peopleColor);
    }


    public Set<String> isWin(int x, int y, String color) {
        if (color.equals("黑")) {
            color = "●";
        }
        if (color.equals("白")) {
            color = "○";
        }
        Set<String> index = new HashSet<>();

        // 橫向
        for (int i = 0; i < board.length - 5; i++) {
            if (board[x][i].equals(color) && board[x][i + 1].equals(color) && board[x][i + 2].equals(color)
                    && board[x][i + 3].equals(color) && board[x][i + 4].equals(color)) {
                for (int j = 0; j < 5; j++) {
                    index.add(x + "," + (i + j));
                }
                return index;
            }
        }
        // 直向
        for (int i = 0; i < board.length - 5; i++) {
            if (board[i][y].equals(color) && board[i + 1][y].equals(color) && board[i + 2][y].equals(color)
                    && board[i + 3][y].equals(color) && board[i + 4][y].equals(color)) {
                for (int j = 0; j < 5; j++) {
                    index.add((i + j) + "," + y);
                }
                return index;
            }
        }

        //斜向「/」
        int n = 1;
        int i = 1;
        int end = 0;
        index.add(x + "," + y);
        while (end < 2 && n < 5) {
            if (end == 0) {
                if (y - i < 0 || x + i > 14) {
                    end++;
                    continue;
                }
                if (board[x + i][y - i].equals(color)) {
                    index.add((x + i) + "," + (y - i));
                    i++;
                    n++;
                } else {
                    end++;
                    i = 1;
                }
            } else if (end == 1) {
                if (x - i < 0 || y + i > 14) {
                    end++;
                    continue;
                }
                if (board[x - i][y + i].equals(color)) {
                    index.add((x - i) + "," + (y + i));
                    n++;
                    i++;
                } else {
                    end++;
                }
            }
        }

        if (n == 5) {
            return index;
        }

        index.clear();
        index.add(x + "," + y);
        //斜向「\」
        n = 1;
        i = 1;
        end = 0;

        while (end < 2 && n < 5) {

            if (end == 0) {

                if (x + i > 14 || y + i > 14) {
                    end++;

                    continue;
                }
                if (board[x + i][y + i].equals(color)) {

                    index.add((x + i) + "," + (y + i));
                    i++;
                    n++;
                } else {
                    end++;
                    i = 1;
                }
            } else if (end == 1) {

                if (x - i < 0 || y - i < 0) {
                    end++;

                    continue;
                }
                if (board[x - i][y - i].equals(color)) {

                    index.add((x - i) + "," + (y - i));
                    n++;
                    i++;
                } else {
                    end++;
                }
            }

        }

        if (n == 5) {
            return index;
        } else {
            return null;
        }
    }


    public boolean isOk(int x, int y) {
        return board[x - 1][y - 1].equals("0");
    }

    private String pieceColor(String color) {
        if (color.equals("黑")) {
            return "●";
        }
        if (color.equals("白")) {
            return "○";
        }
        return null;
    }

    private Color getColor(String s) {
        switch (s) {
            case "黑":
                return Color.black;
            case "白":
                return Color.WHITE;
            case "紅":
                return Color.RED;
            default:
                return null;
        }
    }

    private String getWhoPiece(String s) {
        if (s.equals("黑")) {
            return "白";
        } else {
            return "黑";
        }
    }

    private String getUrl() {
        try {

            cloudinary.uploader().destroy(channel.getId(), ObjectUtils.asMap("resource_type", "image"));

            return (String) new JSONObject(cloudinary.uploader().
                    unsignedUpload(new File("Game/" + channel.getId() + ".png"),
                            "default-preset",
                            ObjectUtils.asMap("public_id", channel.getId()))).get("secure_url");
        } catch (Exception e) {
            return null;
        }
    }

    public void end() {

        try {
            cloudinary.uploader().destroy(channel.getId(), ObjectUtils.asMap("resource_type", "image"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new File("Game/" + channel.getId() + ".png").delete();
        channel.sendMessage("對戰已中止").setActionRow(
                Button.primary("confirm","刪除頻道")
        ).queue();

        GoBang.gameManager.playerGameMap.remove(player1);
        GoBang.gameManager.playerGameMap.remove(player2);
        GoBang.gameManager.player.remove(player1);
        GoBang.gameManager.player.remove(player2);
        GoBang.gameManager.map.remove(channel.getId());

    }
}
