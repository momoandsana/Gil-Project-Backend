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
    private String userImageUrl;
    private String content;
    private LocalDateTime date;
    private Integer state;

    public NotificationResDTO(Notification notification) {
        this.id = notification.getId();
        this.userId = notification.getUser().getId();
        this.userImageUrl = notification.getUser().getImageUrl();
        this.content = notification.getContent();
        this.date = notification.getDate();
        this.state = notification.getState();

    }

}
