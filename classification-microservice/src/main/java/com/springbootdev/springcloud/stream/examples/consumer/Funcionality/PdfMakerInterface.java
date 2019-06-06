package com.springbootdev.springcloud.stream.examples.consumer.Funcionality;


import com.lowagie.text.Document;
import com.springbootdev.springcloud.stream.examples.consumer.Model.classifyDocument;

import java.net.URL;
import java.util.List;

public interface PdfMakerInterface {
    classifyDocument makePdf(List<URL> urls, String userid, String studentid, boolean makeClassify);
}
