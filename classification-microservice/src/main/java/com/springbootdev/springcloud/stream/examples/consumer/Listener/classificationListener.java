package com.springbootdev.springcloud.stream.examples.consumer.Listener;

import com.lowagie.text.Document;
import com.springbootdev.springcloud.stream.examples.consumer.Funcionality.PdfMakerInterface;
import com.springbootdev.springcloud.stream.examples.consumer.Funcionality.classificationInterface;
import com.springbootdev.springcloud.stream.examples.consumer.Model.DocumentModel;
import com.springbootdev.springcloud.stream.examples.consumer.Model.Task;
import com.springbootdev.springcloud.stream.examples.consumer.Model.classifyDocument;
import com.springbootdev.springcloud.stream.examples.consumer.Repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.PropertySource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@PropertySource("classpath:classification.properties")
@EnableBinding(Sink.class)
public class classificationListener {

    private static final Logger logger = LoggerFactory.getLogger(classificationListener.class);
    private PdfMakerInterface pdfMaker;
    private DocumentRepository documentRepo;

    @Value("${urlForUpload}")
    private String uploadPdfUrl;
    @Autowired
    public classificationListener(PdfMakerInterface pdfMaker, DocumentRepository documentRepository) {
        this.pdfMaker = pdfMaker;
        this.documentRepo = documentRepository;
    }


    @StreamListener(target = Sink.INPUT)
    public void listenForTask(Task task) {

        logger.info(" received new task [" + task.getUuid().toString() + "] ");
        boolean makeClassify = true;
        logger.info(task.getCategory());
        if (task.getCategory().equals(""))
            makeClassify = false;

        classifyDocument document = pdfMaker.makePdf(task.getUrls(), task.getStudentid(), task.getUserid(), makeClassify);
        logger.info("Pdf document has been created ");

        DocumentModel documentToSave = new DocumentModel().withCategory(document.getCategory()).withDate(LocalDateTime.now()).withStudentId(task.getStudentid()).withUserId(task.getUserid());
        documentRepo.save(documentToSave);

    }
}
