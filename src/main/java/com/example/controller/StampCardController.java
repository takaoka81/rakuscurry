package com.example.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.LoginUserDetails;
import com.example.domain.User;
import com.example.enums.StampRank;
import com.example.service.StampService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stamp")
public class StampCardController {
    private final StampService stampService;

    @RequestMapping("/showStampCard")
    public String showStampCard(@AuthenticationPrincipal LoginUserDetails loginUserDetails, Model model) {
        User user = loginUserDetails.getUser();

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
