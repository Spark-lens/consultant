package com.itheima.consultant.config;

import com.itheima.consultant.aiservices.ConsultantService;
import com.itheima.consultant.repository.RedisChatMemoryStore;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Copyright (C), 2021, 北京同创永益科技发展有限公司
 *
 * @author YueMing
 * @version 3.0.0
 * @description
 * @date $ $
 */
@Configuration
public class CommonConfig {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Autowired
    private ChatMemoryStore redisChatMemoryStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private RedisEmbeddingStore redisEmbeddingStore;

//    @Bean
//    public ConsultantService consultantService(){
//        ConsultantService cs = AiServices.builder(ConsultantService.class)
//                .chatModel(openAiChatModel)     // 设置对话时使用的模型对象
//                .build();
//        return cs;
//    }


    /**
     * 定义会话记忆对象
     */
    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }


    /**
     * 定义会话记忆对象提供者
     *
     * 初始化、新增 都会调用此方法，因为都会涉及到 新的memoryId
     */
    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        ChatMemoryProvider chatMemoryProvider = new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)           // id值
                        .maxMessages(20)        // 最大会话记录数量
                        .chatMemoryStore(redisChatMemoryStore)      // 配置 ChatMemoryStore
                        .build();
            }
        };
        return chatMemoryProvider;
    }


    /**
     * 创建向量数据库操作对象
     * @return
     */
//    @Bean
    public EmbeddingStore store(){
        // 1.加载文档进内存
//        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content");
//        List<Document> documents = FileSystemDocumentLoader.loadDocuments("D:\\Projects_java\\langchain4j_heima\\consultant\\src\\main\\resources\\content");
        // 加载文档时，指定解析器
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content",new ApachePdfBoxDocumentParser());

        // 2.构建向量数据库操作对象，操作的是内存版本的向量数据库
        // InMemory 内存、Embedding 向量、Store 存储
        // InMemoryEmbeddingStore 操作内存向量数据库
//        InMemoryEmbeddingStore store = new InMemoryEmbeddingStore();

        // 3.构建文档分割器对象
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(5000, 100);

        // 4.构建一个 EmbeddingStoreIngestor 对象，完成文本数据的切割、向量化、存储
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
//                .embeddingStore(store)
                .embeddingStore(redisEmbeddingStore)
                .documentSplitter(documentSplitter)     // 配置文本分割器对象
                .embeddingModel(embeddingModel)         // 配置向量化模型对象
                .build();
        ingestor.ingest(documents);
        return redisEmbeddingStore;
    }

    /**
     * 创建内容检索对象
     * @return
     */
    @Bean
    public ContentRetriever contentRetriever(/*EmbeddingStore store*/){
        return EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(store)      // 设置向量数据库操作对象
                .embeddingStore(redisEmbeddingStore)      // 设置向量数据库操作对象
                .embeddingModel(embeddingModel)     // 设置向量化模型对象
                .minScore(0.5)              // 设置最小分数
                .maxResults(3)              // 设置最大片段数量
                .build();
    }




}
