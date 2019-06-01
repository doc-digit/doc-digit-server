package com.springbootdev.springcloud.stream.examples.consumer.Funcionality;


import com.lowagie.text.Document;

import java.net.URL;
import java.util.List;

public interface PdfMakerInterface {
    Document makePdf(List<URL> urls, String userid, String studentid);
}
