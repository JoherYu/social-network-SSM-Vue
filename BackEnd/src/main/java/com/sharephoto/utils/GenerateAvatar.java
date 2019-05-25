package com.sharephoto.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GenerateAvatar {

    private enum sizes {
        SMALL("_s", 30), MEDIUM("_m", 100), LARGE("_l", 200);
        // 成员变量
        private String suffix;
        private int size;
        // 构造方法 ,赋值给成员变量
        sizes(String suffix, int size) {
            this.suffix = suffix;
            this.size = size;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    public static String[] generateAvatar(String username) throws IOException {
        Identicon identicon = new Identicon();

        Hasher hasher = Hashing.md5().newHasher();
        hasher.putString(username, Charsets.UTF_8);
        String md5 = hasher.hash().toString();

        for (sizes size : sizes.values()){

            BufferedImage image = identicon.create(md5, size.getSize());
            File file = new File(System.getProperty("joher") + "/uploads/avatars/");
            if(!file.exists()) {
                file.mkdirs();
            }
            ImageIO.write(image, "JPG", new File(System.getProperty("joher") + "/uploads/avatars/" + username + size.getSuffix() + ".jpg"));
        }

        String[] filenames = new String[3];
        filenames[0] = username + "_s.jpg";
        filenames[1] = username + "_m.jpg";
        filenames[2] = username + "_l.jpg";
        return filenames;



    }


}
