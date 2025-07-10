package com.itheima.consultant.controller;

import com.itheima.consultant.aiservices.ConsultantService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Copyright (C), 2021, 北京同创永益科技发展有限公司
 *
 * @author YueMing
 * @version 3.0.0
 * @description
 * @date $ $
 */

@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    // 注入 OpenAiChatModel
    @Autowired
    private OpenAiChatModel openAiChatModel;

    @RequestMapping("/chatOpenAiChatModel")
    public String chat(@RequestParam String message) {
        String result = openAiChatModel.chat(message);
        return result;
    }


    /**
     * 使用 AiServices
     */
    @Autowired
    private ConsultantService consultantService;

//    @RequestMapping("/chatAiService")
//    public String chatAiService(@RequestParam String message){
//        String result = consultantService.chat(message);
//        return result;
//    }

    /**
     * 流式调用模式
     */
    @RequestMapping(value = "/chatStreamAiService", produces = "text/html;charset=UTF-8")
    public Flux<String> chatStreamAiService(String memoryId,
                                            String message) {
        Flux<String> result = consultantService.chat(memoryId, message);
        return result;
    }


}
