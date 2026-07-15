package com.example.izibus.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditTable {
    @CreatedDate
    @Column(nullable = true)
    private LocalDate createDate;
    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime updateDate;
    @CreatedBy
    @Column(nullable = true)
    private String createBy;
    @LastModifiedBy
    @Column(nullable = true)
    private String updateBy;
}