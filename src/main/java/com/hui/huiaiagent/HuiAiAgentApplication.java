package com.hui.huiaiagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class HuiAiAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(HuiAiAgentApplication.class, args);
	}

}
