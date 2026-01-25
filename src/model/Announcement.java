package model;

import java.sql.Timestamp;

public class Announcement {
    private int id;
    private String title;
    private String content;
    private Timestamp createdAt;
    private String type;

    public Announcement(int id, String title, String content, Timestamp createdAt, String type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.type = type;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getType() { return type; }
}