package com.springbootdev.springcloud.stream.examples.consumer.Model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.net.URL;
import java.util.UUID;

public class Task implements Serializable {
    public UUID getuuid() {
        return uuid;
    }

    public URL getUrl() {
        return url;
    }

    @JsonProperty("id")
    private UUID uuid;

    @JsonProperty("url")
    private URL url;
}
