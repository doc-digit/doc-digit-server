package com.springbootdev.springcloud.stream.examples.consumer.Listener;

import com.lowagie.text.Document;
import com.springbootdev.springcloud.stream.examples.consumer.Funcionality.PdfMakerInterface;
import com.springbootdev.springcloud.stream.examples.consumer.Model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;


@EnableEurekaClient
@EnableBinding(Sink.class)
public class classificationListener {

    private static final Logger logger = LoggerFactory.getLogger(classificationListener.class);
    private PdfMakerInterface pdfMaker;

    @Autowired
    public classificationListener(PdfMakerInterface pdfMaker) {
        this.pdfMaker = pdfMaker;
    }


    @StreamListener(target = Sink.INPUT)
    public void listenForTask(Task task) {

        logger.info(" received new task [" + task.getUuid().toString() + "] ");
        Document document = pdfMaker.makePdf(task.getUrls(), task.getStudentid(), task.getUserid());
        logger.info("Pdf document has been created");


    }
}
