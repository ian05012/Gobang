package gobang;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class GUI extends Canvas implements GUIInterface {

    BufferedImage bufferedImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);


    public void paint(String id) {
        // TODO Auto-generated method stub
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(Color.ORANGE);
        g2d.fillRect(0,0,500,500);
        g2d.setColor(Color.black);
        g2d.drawRect(startX, startY, size, size);

        for (int i = 1; i < 16; i++) {
            g2d.drawString(String.valueOf((char) (i + 64)), (startX - 2) + 30 * (i - 1), startY - 5);
            if(i <10) {
                g2d.drawString(String.valueOf(i), startX - 15, (startY + 2) + 30 * (i - 1));
            }else{
                g2d.drawString(String.valueOf(i), startX - 17, (startY + 2) + 30 * (i - 1));
            }
            if (i > 14) continue;
            g2d.drawLine(startX, startY + 30 * i, startX + size, startY + 30 * i);
            g2d.drawLine(startX + 30 * i, startY, startX + 30 * i, startY + size);
        }

        g2d.dispose();
        save(id);
    }

    public void paintPiece(int x, int y,String color,String num) {
        // TODO Auto-generated method stub
        Graphics2D g2d = bufferedImage.createGraphics();

        switch (color) {
            case "黑":
                g2d.setColor(Color.black);
                g2d.fillOval(startX - 10 + x * 30, startY - 10 + y * 30, 20, 20);
                break;
            case "白":
                g2d.setColor(Color.WHITE);
                g2d.fillOval(startX - 10 + x * 30, startY - 10 + y * 30, 20, 20);
                break;
            case "紅":
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawOval(startX - 10 + x * 30, startY - 10 + y * 30, 20, 20);
                break;
        }
        g2d.dispose();
        save(num);
    }


    public void save(String name) {
        RenderedImage rendImage = bufferedImage;


        try {
            File file = new File("Game/" + name + ".png");
            ImageIO.write(rendImage, "png", file);
        } catch (IOException ignored) {
        }
    }
}
