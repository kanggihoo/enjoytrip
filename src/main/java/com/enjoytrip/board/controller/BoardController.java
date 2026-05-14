package com.enjoytrip.board.controller;

import com.enjoytrip.board.dto.Board;
import com.enjoytrip.board.service.BoardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/boards")
    public String list(String type, Model model) {
        type = normalizeType(type);
        model.addAttribute("boardList", boardService.getList(type));
        model.addAttribute("type", type);
        return "board/list";
    }

    @GetMapping("/boards/new")
    public String writeForm(String type, HttpSession session, Model model) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("type", normalizeType(type));
        return "board/write";
    }

    @PostMapping("/boards")
    public String write(@ModelAttribute Board board, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        board.setType(normalizeType(board.getType()));
        board.setUserId(loginUser);
        int boardId = boardService.write(board);
        return "redirect:/boards/" + boardId + "?type=" + board.getType();
    }

    @GetMapping("/boards/{boardId}")
    public String detail(@PathVariable int boardId, String type, Model model) {
        model.addAttribute("board", boardService.getDetail(boardId));
        model.addAttribute("type", normalizeType(type));
        return "board/detail";
    }

    @GetMapping("/boards/{boardId}/edit")
    public String editForm(@PathVariable int boardId, String type, HttpSession session, Model model) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        type = normalizeType(type);
        Board board = boardService.getDetail(boardId);
        if (!loginUser.equals(board.getUserId())) {
            return "redirect:/boards/" + boardId + "?type=" + type;
        }
        model.addAttribute("board", board);
        model.addAttribute("type", type);
        return "board/modify";
    }

    @PostMapping("/boards/{boardId}")
    public String modify(@PathVariable int boardId, @ModelAttribute Board board, String type, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        type = normalizeType(type);
        board.setBoardId(boardId);
        boardService.modify(board, loginUser);
        return "redirect:/boards/" + boardId + "?type=" + type;
    }

    @PostMapping("/boards/{boardId}/delete")
    public String delete(@PathVariable int boardId, String type, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        type = normalizeType(type);
        boardService.remove(boardId, loginUser);
        return "redirect:/boards?type=" + type;
    }

    @GetMapping("/board/list")
    public String legacyList(String type) {
        return "redirect:/boards?type=" + normalizeType(type);
    }

    @GetMapping("/board/detail")
    public String legacyDetail(Integer boardId, String type) {
        if (boardId == null) {
            return "redirect:/boards?type=" + normalizeType(type);
        }
        return "redirect:/boards/" + boardId + "?type=" + normalizeType(type);
    }

    @GetMapping("/board/write")
    public String legacyWrite(String type) {
        return "redirect:/boards/new?type=" + normalizeType(type);
    }

    @GetMapping("/board/modify")
    public String legacyModify(Integer boardId, String type) {
        if (boardId == null) {
            return "redirect:/boards?type=" + normalizeType(type);
        }
        return "redirect:/boards/" + boardId + "/edit?type=" + normalizeType(type);
    }

    @GetMapping("/board/delete")
    public String legacyDelete() {
        return "redirect:/boards";
    }

    private String normalizeType(String type) {
        if ("notice".equals(type)) {
            return "notice";
        }
        return "free";
    }
}
