package com.enjoytrip.common.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class KakaoModelAttributeAdvice {

    private final String kakaoJavascriptKey;

    public KakaoModelAttributeAdvice(@Value("${kakao.javascript-key:}") String kakaoJavascriptKey) {
        this.kakaoJavascriptKey = kakaoJavascriptKey;
    }

    @ModelAttribute("kakaoJavascriptKey")
    public String kakaoJavascriptKey() {
        return kakaoJavascriptKey;
    }
}
