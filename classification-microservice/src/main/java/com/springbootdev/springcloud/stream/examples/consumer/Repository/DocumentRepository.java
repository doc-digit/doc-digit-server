package com.springbootdev.springcloud.stream.examples.consumer.Repository;

import com.springbootdev.springcloud.stream.examples.consumer.Model.DocumentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentModel, Long> {
}
