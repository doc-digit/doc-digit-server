package com.springbootdev.springcloud.stream.examples.consumer.Listener;

import com.lowagie.text.Document;
import com.springbootdev.springcloud.stream.examples.consumer.Funcionality.PdfMakerInterface;
import com.springbootdev.springcloud.stream.examples.consumer.Funcionality.classificationInterface;
import com.springbootdev.springcloud.stream.examples.consumer.Model.DocumentModel;
import com.springbootdev.springcloud.stream.examples.consumer.Model.Task;
import com.springbootdev.springcloud.stream.examples.consumer.Repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import java.time.LocalDateTime;
import java.util.Date;


@EnableBinding(Sink.class)
public class classificationListener {

    private static final Logger logger = LoggerFactory.getLogger(classificationListener.class);
    private PdfMakerInterface pdfMaker;
    private DocumentRepository documentRepo;
    private classificationInterface classification;

    @Autowired
    public classificationListener(PdfMakerInterface pdfMaker, DocumentRepository documentRepository) {
        this.pdfMaker = pdfMaker;
        this.documentRepo = documentRepository;
    }


    @StreamListener(target = Sink.INPUT)
    public void listenForTask(Task task) {

        logger.info(" received new task [" + task.getUuid().toString() + "] ");
        Document document = pdfMaker.makePdf(task.getUrls(), task.getStudentid(), task.getUserid());
        logger.info("Pdf document has been created");
        String category;
        if (task.getCategory() == null)
            category = classification.classify(document);
        else
            category = task.getCategory();
        DocumentModel documentToSave = new DocumentModel().withCategory(category).withDate(LocalDateTime.now()).withStudentId(task.getStudentid()).withUserId(task.getUserid());
        documentRepo.save(documentToSave);

    }
}
