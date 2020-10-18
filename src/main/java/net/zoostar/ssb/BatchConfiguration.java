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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableBatchProcessing
public class BatchConfiguration {

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
	public Job jobEchoMessage(JobExecutionListener listener, ItemReader<String> itemReader) {
		return jobBuilderFactory.get("echoMessage").
				incrementer(new RunIdIncrementer()).
				listener(listener).
				start(step1(itemReader)).
				build();
	}
	

	protected Step step1(ItemReader<String> itemReader) {
		return stepBuilderFactory.get("step1").
			    <String, String> chunk(10).
			    reader(itemReader).
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
	
	
	@Bean
	public FlatFileItemReader<String> itemReader(Resource resource) {
    	FlatFileItemReader<String> itemReader = new FlatFileItemReader<>();
    	itemReader.setLineMapper(new LineMapper<String>() {

			@Override
			public String mapLine(String line, int lineNumber) throws Exception {
				log.debug("Mapping line: {}", lineNumber);
				return line;
			}
    		
    	});
    	itemReader.setLinesToSkip(1);
    	itemReader.setResource(resource);
    	return itemReader;
	}
	
	@Bean
	@StepScope
	@Profile("dev")
	public FileSystemResource fileSystemResource(@Value("#{jobParameters['resource']}") String resource) {
		log.info("fileSystemResource({})", resource);
		return new FileSystemResource(resource);
	}

}
