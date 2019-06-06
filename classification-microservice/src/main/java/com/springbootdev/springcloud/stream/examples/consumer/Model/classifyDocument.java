package com.springbootdev.springcloud.stream.examples.consumer.Model;

import java.io.ByteArrayOutputStream;

public class classifyDocument {
    private ByteArrayOutputStream document;
    private String category;

    public classifyDocument(ByteArrayOutputStream document, String category) {
        this.document = document;
        this.category = category;
    }

    public ByteArrayOutputStream getDocument() {
        return document;
    }

    public void setDocument(ByteArrayOutputStream document) {
        this.document = document;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
