package com.springbootdev.springcloud.stream.examples.consumer.Model;


import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.net.URL;
import java.util.Date;

@Entity
@Table(name = "DOCUMENTS")
public class Document {
    @Id
    private int Id;
    @Column(name = "created_date")
    private Date date;
    @Column(name = "document_category")
    private String documentCategory;
    @Column(name = "student")
    private int studentId;
    @Column(name = "userid")
    private int userid;
}
