package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.StampRank;
import com.example.domain.User;
import com.example.service.StampService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/stamp")
public class StampCardController {

    @Autowired
    private StampService stampService;

    @Autowired
    private HttpSession session;

    @RequestMapping("/showStampCard")
    public String showStampCard(Model model) {
        User user = (User) session.getAttribute("user");

        Integer freeCurryCount = stampService.getFreeCurryCount(user.getStampNowCount());
        Integer stampOnCardCount = stampService.getStampOnCardCount(user.getStampNowCount());
        String rankLabel = StampRank.fromStampCount(user.getStampAllCount()).getLabel();
        String labelColor = StampRank.fromStampCount(user.getStampAllCount()).getColorHex();

        model.addAttribute("freeCurryCount", freeCurryCount);
        model.addAttribute("stampOnCardCount", stampOnCardCount);
        model.addAttribute("rankLabel", rankLabel);
        model.addAttribute("labelColor", labelColor);

        return "/stamp/stamp_card";
    }

}
