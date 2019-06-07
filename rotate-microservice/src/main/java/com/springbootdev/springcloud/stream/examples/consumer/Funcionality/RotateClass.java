package com.springbootdev.springcloud.stream.examples.consumer.Funcionality;

import com.recognition.software.jdeskew.ImageDeskew;
import com.springbootdev.springcloud.stream.examples.consumer.Listener.rotatemicroservicelistener;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.TessAPI1;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@PropertySource("classpath:rotating.properties")
public class RotateClass implements RotateClassInterface {


    private static final Logger logger = LoggerFactory.getLogger(rotatemicroservicelistener.class);


    @Value("${language}")
    private String language = "eng";
    @Value("${datapath}")
    private String datapath;
    @Value("${angle}")
    private double MINIMUM_DESKEW_THRESHOLD;
    @Value("${rotatingIterations}")
    private int NUMBER_OF_ITERATIONS;

    private BufferedImage correctingRotate(BufferedImage image) {
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            //Initializing image to rotate
            ImageDeskew imageDeskew = new ImageDeskew(image);
            //Calculate angle of image to rotate
            double imageSkewAngle = imageDeskew.getSkewAngle();
            // Rotate image if its angle is bigger then minimum angle
            if ((imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -(MINIMUM_DESKEW_THRESHOLD))) {
                image = ImageHelper.rotateImage(image, -imageSkewAngle);
            }

        }
        return image;
    }

    private BufferedImage rotating(BufferedImage image) {
        ITessAPI.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        IntBuffer orientation = IntBuffer.allocate(1);
        IntBuffer direction = IntBuffer.allocate(1);
        IntBuffer order = IntBuffer.allocate(1);
        FloatBuffer deskew_angle = FloatBuffer.allocate(1);
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        logger.info("Buffers initialization");
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);

        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessAPI1.TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);


        TessAPI1.TessPageIterator pi = TessAPI1.TessBaseAPIAnalyseLayout(handle);


        if (pi == null) {
            return image;
        }
        TessAPI1.TessPageIteratorOrientation(pi, orientation, direction, order, deskew_angle);

        int orient = orientation.get();
        logger.info("Orientation: " + orient);
        if (orient == 1) {
            ImageHelper.rotateImage(image, 90);
        } else if (orient == 2) {
            ImageHelper.rotateImage(image, 180);
        } else if (orient == 3) {
            ImageHelper.rotateImage(image, 270);
        }

        return image;
    }

    @Override
    public BufferedImage rotateImage(BufferedImage image) {

        BufferedImage newimage = rotating(image);

        newimage = correctingRotate(newimage);
        //Initializing white page which fill page to rectangle
        int imageWidth = newimage.getWidth();
        int imageHeight = newimage.getHeight();

        BufferedImage background = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D backrouundDraw = background.createGraphics();
        backrouundDraw.setBackground(Color.WHITE);
        backrouundDraw.clearRect(0, 0, imageWidth, imageHeight);

        // Initializing combined graphic
        BufferedImage combinedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics mergedGraphic = combinedImage.getGraphics();
        //Merging background with text creating rectangle page
        mergedGraphic.drawImage(background, 0, 0, null);
        mergedGraphic.drawImage(newimage, 0, 0, null);

        //return full image
        return combinedImage;
    }
}
