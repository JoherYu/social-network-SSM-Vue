package com.sharephoto.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GenerateImage {

    public static void generateImage(String filename){

        BufferedImage bi = new BufferedImage(800, 800, BufferedImage.TYPE_INT_BGR);

        File fileM = new File(System.getProperty("joher") + "/uploads");
        if(!fileM.exists()) {
            fileM.mkdirs();
        }

        final File file = new File(System.getProperty("joher") + "/uploads/" + filename);
        System.out.println(file.getAbsolutePath());

        try {
            if(file.exists()) {
                file.delete();
                file.createNewFile();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }


        writeImage(bi, "jpg", file);
        System.out.println("绘图成功");
    }


    public static boolean writeImage(BufferedImage bi, String picType, File file) {

        Graphics2D g = (Graphics2D) bi.getGraphics();


        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, 800, 800);
        //g.setPaint(Color.BLUE);
        g.setColor(new Color((int)(128+Math.random()*(127)), (int)(128+Math.random()*(127)), (int)(128+Math.random()*(127))));
        g.fillRect(0, 0, 800,800);
        g.dispose();
        boolean val = false;
        try {
            val = ImageIO.write(bi, picType, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return val;
    }

}