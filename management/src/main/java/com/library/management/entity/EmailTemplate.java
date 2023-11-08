package com.library.management.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "email_template")
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "temp_id")
    private int tempId;
    @Column(name = "template",columnDefinition = "TEXT")
    private String template;
    @Column(name = "template_key")
    private String templateKey;
    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;
    @UpdateTimestamp
    @Column(name = "updated_on")
    private Date updatedOn;

}
