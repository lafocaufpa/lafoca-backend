package com.ufpa.lafocabackend.domain.listener;

import com.ufpa.lafocabackend.domain.events.addedStudentEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class studentAddedListener {

    @EventListener
    public void whenAddingMember(addedStudentEvent event) {
        System.out.println("Evento disparado");
    }
}
