package com.hui.huiaiagent.demo.invoke;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.io.ClassPathResource;

/**
 * 原生 SDK 演示用的 API Key。
 * 自动从 classpath 下的 application-local.yml 读取 spring.ai.dashscope.api-key，
 * 该文件已被 git 忽略，真实密钥不会进入仓库。
 */
public interface TestApiKey {

    String API_KEY = loadApiKey();

    private static String loadApiKey() {
        try {
            return new YamlPropertySourceLoader()
                    .load("application-local", new ClassPathResource("application-local.yml"))
                    .get(0)
                    .getProperty("spring.ai.dashscope.api-key")
                    .toString();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "读取 src/main/resources/application-local.yml 中的 spring.ai.dashscope.api-key 失败，"
                            + "请确认该文件存在且格式正确", e);
        }
    }
}
