package com.library.management.entity;

import com.library.management.dto.UserHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;
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
@Table(name = "user_history")
public class UserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hist_id")
    private long uHistId;
    @Column(name = "username")
    private String username;
    @Column(name = "role")
    private String role;
    @Column(name = "note")
    private String note;

    @Column(name = "created_by")
    private String createdBy;
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;


    public UserHistory convertDtoToEntity(UserHistoryDto dto){
        UserHistory history = new UserHistory();
        history.setUsername(dto.getUsername());
        history.setRole(dto.getRole());
        history.setNote(dto.getNote());
        history.setCreatedBy(dto.getCreatedBy());
        return history;
    }

}
