package com.springboot.config;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter //상속해서 코드레벨에는 생성이 안되는데 테이블은생성
@MappedSuperclass //매핑정보만 제공하는 (매핑만 하는) 부모클래스
@EntityListeners(AuditingEntityListener.class) //엔티티의 crud를 감지해서 엔티티가생성 수정 삭제 등을 파악하고 해당 객체만 실행될 수 있도록 관리, 영속성의 상태변화도 감지
public abstract class BaseEntity {

    @CreatedDate
    private LocalDateTime createsAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;
}

