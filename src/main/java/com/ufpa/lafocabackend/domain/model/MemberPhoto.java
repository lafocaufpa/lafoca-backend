package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
public class MemberPhoto extends Photo {

}
