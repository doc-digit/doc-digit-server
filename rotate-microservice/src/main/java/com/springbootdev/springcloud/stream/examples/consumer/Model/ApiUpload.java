package com.springbootdev.springcloud.stream.examples.consumer.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.net.URL;
import java.util.UUID;

public class ApiUpload implements Serializable {
    @JsonProperty("id")
    private UUID uuid;

    @JsonProperty("url")
    private URL url;


    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
