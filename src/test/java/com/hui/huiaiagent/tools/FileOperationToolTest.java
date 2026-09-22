package com.hui.huiaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FileOperationToolTest {

    @Test
    public void testReadFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "程序员derder.txt";
        String result = tool.readFile(fileName);
        System.out.println("result: == " + result);
        assertNotNull(result);
    }

    @Test
    public void testWriteFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "程序员derder.txt";
        String content = "https://www.codefather.cn 程序员derder学习交流社区";
        String result = tool.writeFile(fileName, content);
        assertNotNull(result);
    }
}