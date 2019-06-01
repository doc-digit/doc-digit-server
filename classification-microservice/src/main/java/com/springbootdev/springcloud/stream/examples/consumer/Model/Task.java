package com.springbootdev.springcloud.stream.examples.consumer.Model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class Task implements Serializable {
    @JsonProperty("id")
    private UUID uuid;

    @JsonProperty("url")
    private List<URL> urls;

    public UUID getUuid() {
        return uuid;
    }

    public List<URL> getUrls() {
        return urls;
    }

    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    private String category;

    @JsonProperty("userid")
    private String userid;

    public String getUserid() {
        return userid;
    }

    public String getStudentid() {
        return studentid;
    }

    @JsonProperty("studentid")
    private String studentid;
}
