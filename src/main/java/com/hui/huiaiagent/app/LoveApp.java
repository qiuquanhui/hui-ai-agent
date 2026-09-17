package com.hui.huiaiagent.app;

import com.hui.huiaiagent.advisor.AuthorizedAdvisor;
import com.hui.huiaiagent.advisor.ForbiddenWordsAdvisor;
import com.hui.huiaiagent.advisor.MyLoggerAdvisor;
import com.hui.huiaiagent.chatmemory.FileBasedChatMemory;
import com.hui.huiaiagent.chatmemory.MysqlChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashscopeChatModel,MysqlChatMemory mysqlChatMemory) {
//        初始化基于内存的对话记忆
//        ChatMemory chatMemory = new InMemoryChatMemory();
        // 初始化基于文件的对话记忆
       // String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        //  ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        //初始化 chatClient
        // 初始化基于数据库的对话记忆
        ChatMemory chatMemory = mysqlChatMemory;
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        // 权限拦截器
                        new AuthorizedAdvisor(Set.of("badGuy")),   // 可传入封禁名单，演示可先写 new AuthorizedAdvisor()
                        // 违规词拦截器
                        new ForbiddenWordsAdvisor(),
                        //记忆拦截器
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志 Advisor，可按需开启
                        new MyLoggerAdvisor()
                        // 自定义推理增强 Advisor，可按需开启
                        // new ReReadingAdvisor()
                )
                .build();
    }

    //使用chatclient 实现AI调用
    public String doChat(String message, String chatId) {

        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                        .param(AuthorizedAdvisor.AUTH_USER_ID_PARAM,"derder")
                )
                .call()
                .chatResponse();

        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);

        return content;
    }

    //结构化输出实体类
    record LoveReport(String title, List<String> suggestions) {
    }

    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

}
