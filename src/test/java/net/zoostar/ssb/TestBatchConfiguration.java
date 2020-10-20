package net.zoostar.ssb;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class TestBatchConfiguration {
	
	@Bean
	@StepScope
	public ClassPathResource classPathResource(@Value("#{jobParameters['resource']}") String resource) {
		log.info("classPathResource({})", resource);
		return new ClassPathResource(resource);
	}

}
