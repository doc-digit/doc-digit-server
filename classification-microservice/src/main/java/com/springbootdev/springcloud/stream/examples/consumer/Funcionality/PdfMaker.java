package com.springbootdev.springcloud.stream.examples.consumer.Funcionality;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Component
public class PdfMaker implements PdfMakerInterface {

    @Override
    public Document makePdf(List<URL> urls, String userid, String studentid) {
        Document document = new Document(PageSize.A4, 25, 25, 25, 25);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (URL url : urls) {
            Image image;
            try {
                BufferedImage imagetmp = ImageIO.read(url);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(imagetmp, "png", baos);
                image = Image.getInstance(baos.toByteArray());

                document.add(image);
            } catch (IOException | DocumentException e) {
                return null;
            }
        }

        document.addCreator(userid);
        document.addAuthor(studentid);
        return document;
    }
}
