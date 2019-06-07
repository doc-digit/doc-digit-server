package com.springbootdev.springcloud.stream.examples.consumer.Funcionality;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.springbootdev.springcloud.stream.examples.consumer.Model.classifyDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Component
public class PdfMaker implements PdfMakerInterface {

    private static final Logger logger = LoggerFactory.getLogger(PdfMaker.class);


    private classificationInterface classification;

    @Autowired
    public PdfMaker(classificationInterface classification) {
        this.classification = classification;
    }

    @Override
    public classifyDocument makePdf(List<URL> urls, String userid, String studentid, boolean makeClassify) {

        String category = "";

        Document document = new Document(PageSize.A4, 25, 25, 25, 25);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.open();
        for (URL url : urls) {
            Image image;
            try {
                BufferedImage imagetmp = ImageIO.read(url);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(imagetmp, "png", baos);
                image = Image.getInstance(baos.toByteArray());
                document.setPageSize(image);
                document.newPage();
                image.setAbsolutePosition(0, 0);
                document.add(image);

                if (makeClassify) {
                    category = classification.classify(imagetmp);
                }

            } catch (IOException | DocumentException e) {
                logger.error(e.getMessage());
                return null;
            }
        }

        document.addCreator(userid);
        document.addAuthor(studentid);
        document.close();
        return new classifyDocument(out, category);
    }
}
