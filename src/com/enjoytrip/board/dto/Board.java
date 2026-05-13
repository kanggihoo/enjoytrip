package com.enjoytrip.board.dto;

public class Board {
    private int    boardId;
    private String type;       // "notice" | "free"
    private String title;
    private String content;
    private String userId;
    private int    views;
    private String createdAt;
    private String updatedAt;

    public Board() {}

    public int    getBoardId()               { return boardId; }
    public void   setBoardId(int boardId)    { this.boardId = boardId; }
    public String getType()                  { return type; }
    public void   setType(String type)       { this.type = type; }
    public String getTitle()                 { return title; }
    public void   setTitle(String title)     { this.title = title; }
    public String getContent()               { return content; }
    public void   setContent(String content) { this.content = content; }
    public String getUserId()                { return userId; }
    public void   setUserId(String userId)   { this.userId = userId; }
    public int    getViews()                 { return views; }
    public void   setViews(int views)        { this.views = views; }
    public String getCreatedAt()             { return createdAt; }
    public void   setCreatedAt(String v)     { this.createdAt = v; }
    public String getUpdatedAt()             { return updatedAt; }
    public void   setUpdatedAt(String v)     { this.updatedAt = v; }
}
