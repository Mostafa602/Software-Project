package com.lms.domain.dto.Notification;

public class NotificationDto {
    private Long id;
    private String content;
    private Boolean isRead;

    NotificationDto( long id, String content, Boolean isRead ){
        this.id = id;
        this.content = content;
        this.isRead = isRead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsRead(){
        return this.isRead;
    }

    public void setIsRead( Boolean isRead){
        this.isRead = isRead;
    }
}