package com.webx.news.managers;

import com.webx.news.models.NewsItem;
import java.util.*;

public class NewsManager {
    private final List<NewsItem> news = new ArrayList<>();
    
    public void addNews(NewsItem item) {
        news.add(item);
    }
    
    public List<NewsItem> getLatestNews(int count) {
        return news.subList(0, Math.min(count, news.size()));
    }
}
