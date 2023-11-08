package com.library.management.repository;

import com.library.management.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<EmailTemplate,Integer> {

    EmailTemplate findByTemplateKey(String bookTemplate);
}
