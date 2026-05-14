package com.enjoytrip.attraction.controller;

import com.enjoytrip.attraction.mapper.AttractionMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class AttractionPageController {

    private final AttractionMapper attractionMapper;

    public AttractionPageController(AttractionMapper attractionMapper) {
        this.attractionMapper = attractionMapper;
    }

    @GetMapping("/attractions")
    public String list(Model model) {
        try {
            model.addAttribute("sidoList", attractionMapper.selectAllSido());
        } catch (RuntimeException e) {
            model.addAttribute("sidoList", Collections.emptyList());
        }
        return "attraction/list";
    }

    @GetMapping("/attractions/{contentId}")
    public String detail(@PathVariable Integer contentId, Model model) {
        model.addAttribute("contentId", contentId);
        return "attraction/detail";
    }

    @GetMapping("/attraction/list")
    public String legacyList() {
        return "redirect:/attractions";
    }

    @GetMapping("/attraction/detail")
    public String legacyDetail(@RequestParam Integer contentId) {
        return "redirect:/attractions/" + contentId;
    }
}
