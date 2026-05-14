package com.enjoytrip.hotplace.controller;

import com.enjoytrip.hotplace.dto.Hotplace;
import com.enjoytrip.hotplace.service.FileStorageService;
import com.enjoytrip.hotplace.service.HotplaceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HotplaceController {

    private final HotplaceService hotplaceService;
    private final FileStorageService fileStorageService;

    public HotplaceController(HotplaceService hotplaceService, FileStorageService fileStorageService) {
        this.hotplaceService = hotplaceService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/hotplaces")
    public String list(Model model) {
        model.addAttribute("hotplaces", hotplaceService.getAllHotplaces());
        return "hotplace/list";
    }

    @GetMapping("/hotplaces/new")
    public String writeForm(HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/user/login";
        }
        return "hotplace/write";
    }

    @PostMapping("/hotplaces")
    public String write(@ModelAttribute Hotplace hotplace,
                        @RequestParam(value = "image", required = false) MultipartFile image,
                        HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        hotplace.setUserId(loginUser);
        hotplace.setImagePath(fileStorageService.store(image));
        int hotplaceId = hotplaceService.registerHotplace(hotplace);
        return "redirect:/hotplaces/" + hotplaceId;
    }

    @GetMapping("/hotplaces/{hotplaceId}")
    public String detail(@PathVariable int hotplaceId, Model model) {
        model.addAttribute("hotplace", hotplaceService.getHotplaceById(hotplaceId));
        return "hotplace/detail";
    }

    @GetMapping("/hotplaces/{hotplaceId}/edit")
    public String editForm(@PathVariable int hotplaceId, HttpSession session, Model model) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("hotplace", hotplaceService.getHotplaceForEdit(hotplaceId, loginUser));
        return "hotplace/modify";
    }

    @PostMapping("/hotplaces/{hotplaceId}")
    public String modify(@PathVariable int hotplaceId,
                         @ModelAttribute Hotplace hotplace,
                         @RequestParam(value = "image", required = false) MultipartFile image,
                         HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        hotplace.setHotplaceId(hotplaceId);
        Hotplace savedHotplace = hotplaceService.getHotplaceForEdit(hotplaceId, loginUser);
        String imagePath = fileStorageService.store(image);
        hotplace.setImagePath(imagePath.isEmpty() ? savedHotplace.getImagePath() : imagePath);
        hotplaceService.modifyHotplace(hotplace, loginUser);
        return "redirect:/hotplaces/" + hotplaceId;
    }

    @PostMapping("/hotplaces/{hotplaceId}/delete")
    public String delete(@PathVariable int hotplaceId, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        hotplaceService.removeHotplace(hotplaceId, loginUser);
        return "redirect:/hotplaces";
    }

    @GetMapping("/hotplace/list")
    public String legacyList() {
        return "redirect:/hotplaces";
    }

    @GetMapping("/hotplace/write")
    public String legacyWrite() {
        return "redirect:/hotplaces/new";
    }

    @GetMapping("/hotplace/detail")
    public String legacyDetail(Integer hotplaceId) {
        if (hotplaceId == null) {
            return "redirect:/hotplaces";
        }
        return "redirect:/hotplaces/" + hotplaceId;
    }

    @GetMapping("/hotplace/modify")
    public String legacyModify(Integer hotplaceId) {
        if (hotplaceId == null) {
            return "redirect:/hotplaces";
        }
        return "redirect:/hotplaces/" + hotplaceId + "/edit";
    }
}
