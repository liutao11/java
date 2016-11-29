package httpServer.classFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by lt on 2016/9/17.
 */
public class ImageSend {
    public static ByteArrayOutputStream readImgOut(String ImageFileUrl,String ImgStyle) throws IOException {
        File f = new File(ImageFileUrl);
        BufferedImage bufferedImage = ImageIO.read(f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage,ImgStyle, out);
        return  out;
    }
}
