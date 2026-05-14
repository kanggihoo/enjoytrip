package com.enjoytrip.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    private int boardId;
    private String type;
    private String title;
    private String content;
    private String userId;
    private int views;
    private String createdAt;
    private String updatedAt;
}
