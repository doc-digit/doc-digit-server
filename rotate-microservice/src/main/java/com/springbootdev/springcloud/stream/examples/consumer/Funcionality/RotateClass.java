package com.springbootdev.springcloud.stream.examples.consumer.Funcionality;

import com.recognition.software.jdeskew.ImageDeskew;
import com.springbootdev.springcloud.stream.examples.consumer.Listener.rotatemicroservicelistener;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
@PropertySource("classpath:rotating.properties")
public class RotateClass implements RotateClassInterface {

    @Value("${angle}")
    private double MINIMUM_DESKEW_THRESHOLD;
    @Value("${rotatingIterations}")
    private int NUMBER_OF_ITERATIONS;


    @Override
    public BufferedImage rotateImage(BufferedImage image) {

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
        //Initializing white page which fill page to rectangle
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        BufferedImage background = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D backrouundDraw = background.createGraphics();
        backrouundDraw.setBackground(Color.WHITE);
        backrouundDraw.clearRect(0, 0, imageWidth, imageHeight);

        // Initializing combined graphic
        BufferedImage combinedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics mergedGraphic = combinedImage.getGraphics();
        //Merging background with text creating rectangle page
        mergedGraphic.drawImage(background, 0, 0, null);
        mergedGraphic.drawImage(image, 0, 0, null);

        //return full image
        return combinedImage;
    }
}
