package com.springbootdev.springcloud.stream.examples.consumer.Funcionality;

import com.lowagie.text.Document;
import com.springbootdev.springcloud.stream.examples.consumer.Listener.classificationListener;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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
            File f = new File("out.png");
            ImageIO.write(image, "PNG", f);

            text = tesseract.doOCR(f);
        } catch (Exception e) {
            logger.trace("Ocr exception", e);
        }


        String category = "";
        Pattern pattern = Pattern.compile("PD_[0-9][0-9]*");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            category = text.substring(matcher.start(), matcher.end());
        }
        logger.info(String.format("Category %s", category));
        return category;
    }
}
