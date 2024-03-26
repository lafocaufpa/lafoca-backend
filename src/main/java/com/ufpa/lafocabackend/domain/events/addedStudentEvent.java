package com.ufpa.lafocabackend.domain.events;

import com.ufpa.lafocabackend.domain.model.Student;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class addedStudentEvent extends ApplicationEvent {

    private Student student;

    public addedStudentEvent(Object source) {
        super(source);
    }
}
