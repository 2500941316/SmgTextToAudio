package com.smg.controller;

import com.smg.Pojo.TextInfo;
import com.smg.service.TextToRedioInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TextToRedioController {

    @Autowired
    TextToRedioInterface textToRedioInterface;

    @PostMapping("textToRedio")
    public String textToRedio(@RequestBody TextInfo textInfo) {

        return textToRedioInterface.textToRedio(textInfo);
    }
}
