package com.itheima.consultant.aiservices;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

// 当 @AiService 注解生效时，LangChain4J会动态生成 ConsultantService 接口的实现类，并将其注册到 Spring 容器中
// 所以可以使用 Autowired 注入

// 普通调用模式
//@AiService(
//        wiringMode = AiServiceWiringMode.EXPLICIT,
//        chatModel = "openAiChatModel"
//)

// 流式调用模式
@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT,          // 手动装配
    chatModel = "openAiChatModel",                      // 指定模型
    streamingChatModel = "openAiStreamingChatModel",    // 指定流式模型
//    chatMemory = "chatMemory"                           // 指定会话记忆
    chatMemoryProvider = "chatMemoryProvider",          // 配置会话记忆对象提供者
    contentRetriever = "contentRetriever",               // 配置向量数据库检索器
    tools = {"reservationTool"}
)
public interface ConsultantService {
    /**
     * 用于聊天的方法
     * @param memoryId 会话ID
     * @param message  用户的输入内容
     * @return
     */
    @SystemMessage(fromResource = "system.txt")
    public Flux<String> chat(@MemoryId String memoryId,
                             @UserMessage String message);

}
