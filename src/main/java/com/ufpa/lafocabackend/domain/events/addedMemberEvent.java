package com.ufpa.lafocabackend.domain.events;

import com.ufpa.lafocabackend.domain.model.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class addedMemberEvent extends ApplicationEvent {

    private Member member;

    public addedMemberEvent(Object source) {
        super(source);
    }
}
