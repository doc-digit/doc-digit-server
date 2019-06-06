package com.springbootdev.springcloud.stream.examples.consumer.Model;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "DOCUMENTS")
public class DocumentModel {

    @Id
    @SequenceGenerator(name = "auto_gen", sequenceName = "A")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "created_date")
    private LocalDateTime date;
    @Column(name = "document_type")
    private String documentCategory;
    @Column(name = "student")
    private String studentId;
    @Column(name = "userID")
    private String userid;

    public DocumentModel() {
    }

    public DocumentModel(LocalDateTime date, String documentCategory, String studentId, String userid) {
        this.date = date;
        this.documentCategory = documentCategory;
        this.studentId = studentId;
        this.userid = userid;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public DocumentModel withDate(final LocalDateTime data) {
        this.date = data;
        return this;
    }

    public DocumentModel withStudentId(String studentId) {
        this.studentId = studentId;
        return this;
    }

    public DocumentModel withUserId(String userId) {
        this.userid = userId;
        return this;
    }

    public DocumentModel withCategory(String category) {
        this.documentCategory = category;
        return this;
    }


}
