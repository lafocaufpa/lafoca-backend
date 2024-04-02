package com.ufpa.lafocabackend.domain.listener;

import com.ufpa.lafocabackend.domain.events.addedMemberEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberAddedListener {

    @EventListener
    public void whenAddingMember(addedMemberEvent event) {
        System.out.println("Evento disparado");
    }
}
