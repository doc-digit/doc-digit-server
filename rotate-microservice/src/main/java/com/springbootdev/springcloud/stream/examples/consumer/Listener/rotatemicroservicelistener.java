package com.springbootdev.springcloud.stream.examples.consumer.Listener;

import com.netflix.ribbon.proxy.annotation.ClientProperties;
import com.springbootdev.springcloud.stream.examples.consumer.Funcionality.RotateClassInterface;
import com.springbootdev.springcloud.stream.examples.consumer.Model.ApiUpload;
import com.springbootdev.springcloud.stream.examples.consumer.Model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;


@EnableBinding(Sink.class)
@PropertySource("classpath:rotating.properties")
public class rotatemicroservicelistener {
    private static final Logger logger = LoggerFactory.getLogger(rotatemicroservicelistener.class);

    private RotateClassInterface rotate;

    @Value("${urlForUpload}")
    private String urlForUpload;
    @Autowired
    public rotatemicroservicelistener(RotateClassInterface rotate) {
        this.rotate = rotate;
    }

    @StreamListener(target = Sink.INPUT)
    public void listenForTask(Task task) {
        //Logging of receive ne task to rotate
        logger.info(" received new task [" + task.getuuid().toString() + "] ");

        //Initializing image from url given in task json
        BufferedImage image;
        try {
            image = ImageIO.read(task.getUrl());
        } catch (IOException e) {
            logger.error("File under given url not found.");
            return;
        }
        //Rotating image
        BufferedImage rotatedImage = rotate.rotateImage(image);
        logger.info(String.format(" ending rotate image of task [ %s ]", task.getuuid().toString()));


        //Getting url for upload image
        RestTemplate restTemplate = new RestTemplate();
        ApiUpload uploading;
        try {
            uploading = restTemplate.getForObject(urlForUpload, ApiUpload.class);
        } catch (ResourceAccessException e) {
            logger.error("No connection to api");
            return;
        }
        if (uploading == null) {
            return;
        }
        logger.info("Getted upload url: [" + uploading.getUrl() + "]");

        // Create the connection and use it to upload the new object using the pre-signed URL.
        try {

            URL url = uploading.getUrl();
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setDoInput(true);
            c.setRequestMethod("PUT");
            c.setDoOutput(true);
            c.setRequestProperty("Content-Type", "image/png");
            c.connect();
            OutputStream output = c.getOutputStream();
            ImageIO.write(rotatedImage, "PNG", output);
            output.close();
            logger.info("HTTP response code: " + c.getResponseCode());


        } catch (IOException e) {
            logger.error("Problem with sending image");
        }
    }

}