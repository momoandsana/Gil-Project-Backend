package com.web.gilproject.dto;

import com.web.gilproject.domain.Notification;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class NotificationResDTO {

    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime date;

    public NotificationResDTO(Notification notification) {
        this.id = notification.getId();
        this.userId = notification.getUser().getId();
        this.content = notification.getContent();
        this.date = notification.getDate();

    }

}
