package net.zoostar.ssb;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	private boolean readingComplete = false;

	@Value("${batch.driver.class.name:org.h2.Driver}")
	protected String batchDriverClassName;

	@Value("${batch.jdbc.url:jdbc:h2:file:./target/task}")
	protected String batchJdbcUrl;

	@Value("${batch.username:}")
	protected String batchUserName;

	@Value("${batch.password:}")
	protected String batchPassword;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	@Qualifier("dataSource")
	public BatchConfigurer batchConfigurer(DataSource dataSource) {
		log.info("{}", "Create bean batchConfigurer...");
		return new DefaultBatchConfigurer(dataSource);
	}

	@Bean
	@Qualifier("dataSource")
	protected DataSource dataSource() {
		log.info("Creating bean dataSource for url: {}...", batchJdbcUrl);
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(batchDriverClassName);
		config.setJdbcUrl(batchJdbcUrl);
		config.setUsername(batchUserName);
		config.setPassword(batchPassword);
		return new HikariDataSource(config);
	}

	@Bean
	public JobExecutionListener jobExecutionListener() {
		return new JobExecutionListenerSupport() {

			@Override
			public void afterJob(JobExecution jobExecution) {
				if(BatchStatus.COMPLETED == jobExecution.getStatus()) {
					log.info("Job completed successfully: {}", jobExecution);
				} else {
					log.error("JOB DID NOT COMPLETE SUCCESSFULLY: {}", jobExecution);
				}
			}

		};
	}

	@Bean
	public Job jobEchoMessage(JobExecutionListener listener) {
		return jobBuilderFactory.get("echoMessage").
				incrementer(new RunIdIncrementer()).
				listener(listener).
				start(step1()).
				build();
	}
	

	protected Step step1() {
		return stepBuilderFactory.get("step1").
			    <String, String> chunk(10).
			    reader(new ItemReader<String>() {

					@Override
					public String read()
							throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
						if(readingComplete) {
							return null;
						}
						readingComplete = true;
						return "Hello World";
					}
			    	
			    }).
			    processor(new ItemProcessor<String, String>() {

					@Override
					public String process(String item) throws Exception {
						if(!"Hello World".equals(item))
							throw new Exception("Item value not same as 'Hello World': " + item);
						
						return item;
					}
			    	
			    }).
			    writer(new ItemWriter<String>() {

					@Override
					public void write(List<? extends String> items) throws Exception {
						for(String item : items) {
							log.info("Item: {}", item);
						}
					}
			    	
			    }).
			    build();
	}
	
}
