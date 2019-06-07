package com.springbootdev.springcloud.stream.examples.consumer.Funcionality;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class classification implements classificationInterface {

    private static final Logger logger = LoggerFactory.getLogger(classification.class);

    private String filepath = "classification-microservice/src/main/resources/tessdata/";
    private Tesseract tesseract = new Tesseract();

    public classification() {
        tesseract.setDatapath(filepath);
        tesseract.setLanguage("pol");
        tesseract.setPageSegMode(1);
    }

    @Override
    public String classify(BufferedImage image) {
        String text = "";
        try {

            text = tesseract.doOCR(resize(image, image.getHeight() * 4, image.getWidth() * 4));
        } catch (Exception e) {
            logger.trace("Ocr exception", e);
        }


        logger.info(text);
        String category = "";
        Pattern pattern = Pattern.compile("PD_[0-9][0-9]*");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            category = text.substring(matcher.start(), matcher.end());
        }
        logger.info(String.format("Category %s", category));
        return category;
    }

    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
