package com.webx.news.models;

import java.time.LocalDateTime;

public class NewsItem {
    private final String title;
    private final String content;
    private final LocalDateTime timestamp;
    
    public NewsItem(String title, String content) {
        this.title = title;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
