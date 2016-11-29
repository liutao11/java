package httpServer.classFile;

import httpServer.conf.Setup;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by lt on 2016/9/10.
 */
public class FaviconImg {
    public static ByteArrayOutputStream readImgOut(String ImageFileUrl) throws IOException{
        File f = new File(ImageFileUrl);
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        if(f.isFile()) {
            BufferedImage bufferedImage = ImageIO.read(f);
            ImageIO.write(bufferedImage, "jpg", out);
        }
        return out;
    }
}
