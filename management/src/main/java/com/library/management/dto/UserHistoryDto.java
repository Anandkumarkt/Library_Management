package com.library.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryDto {
    private long uHistId;
    private String username;
    private String role;
    private String note;
    private String createdBy;

}
