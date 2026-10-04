package com.hui.huiaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;                       // Kryo：高性能序列化框架（对象 ↔ 二进制）
import com.esotericsoftware.kryo.io.Input;                   // Kryo 的"读取管子"：从文件读二进制
import com.esotericsoftware.kryo.io.Output;                  // Kryo 的"写出管子"：往文件写二进制
import org.objenesis.strategy.StdInstantiatorStrategy;       // 绕过构造器创建对象的策略（反序列化时需要）
import org.springframework.ai.chat.memory.ChatMemory;        // 记忆接口：add（记）/ get（忆）/ clear（忘）
import org.springframework.ai.chat.messages.Message;         // 一条对话消息（用户 / AI / 系统说的）

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件持久化的对话记忆
 * 一个会话（chatId）对应一个 .kryo 二进制文件，全存在 BASE_DIR 目录下；
 * 程序重启后文件还在，所以记忆能跨重启
 */
public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;                 // 所有记忆文件的根目录（构造时传入）
    private static final Kryo kryo = new Kryo();   // 全局共用一台"打包机"（static：造一次，所有实例共用）

    static {
        // 不要求预先登记要序列化的类，省事（否则每个类都得 kryo.register(...)）
        kryo.setRegistrationRequired(false);
        // 设置实例化策略：
        // 反序列化时绕过构造器直接造对象——避免"目标类没有无参构造器就还原失败"的问题
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    // 构造对象时，指定文件保存目录
    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;              // 记下根目录，所有笔记都放这
        File baseDir = new File(dir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();             // 目录不存在就一次性建好（含多级目录）
        }
    }

    // ============ 接口三动作之一：记住（把新消息追加进这本笔记） ============
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId); // ① 先读出这本笔记现有内容
        conversationMessages.addAll(messages);                                        // ② 把新消息追加上去
        saveConversation(conversationId, conversationMessages);                       // ③ 整本重新写回文件
    }

    // ============ 接口三动作之二：回忆（取这本笔记最近 lastN 条） ============
    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> allMessages = getOrCreateConversation(conversationId);  // 读出整本笔记
        return allMessages.stream()
                // 跳过前面的（总数 - N）条；总条数不足 N 时跳 0 条（= 全要）
                .skip(Math.max(0, allMessages.size() - lastN))
                .toList();                                                    // 剩下的就是最近 N 条
    }

    // ============ 接口三动作之三：忘记（整本撕掉） ============
    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();               // 删掉 .kryo 文件 = 清空这本笔记
        }
    }

    // 私有工具①：拿到这本笔记的全部消息；文件不存在就当"新笔记本"返回空列表
    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            // 还原：二进制 → List<Message> 对象
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    // 私有工具②：把整本笔记压扁（对象 → 二进制）写进文件
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        // try-with-resources：花括号结束自动关流，不用手写 close()
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 私有工具③：会话编号 → 具体文件。一个 chatId 一本笔记：BASE_DIR/xxx.kryo
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }
}