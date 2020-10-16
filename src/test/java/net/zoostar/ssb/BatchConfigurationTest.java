package net.zoostar.ssb;

import java.security.SecureRandom;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BatchConfiguration.class })
public class BatchConfigurationTest {
	
	private SecureRandom secureRandom = new SecureRandom();
	
	@Autowired
	JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    JobRepositoryTestUtils jobRepositoryTestUtils;
  
    @AfterEach
    void cleanup() {
    	jobRepositoryTestUtils.removeJobExecutions();
    }
    
	@Test
	void testSuccessfulJobEchoMessage() throws Exception {
		log.info("{}", "Begin testSuccessfulJobEchoMessage...");
		
		// given
		JobParameters parameters = new JobParametersBuilder().
				addLong("random", secureRandom.nextLong()).
				addString("resource", "data/input.txt").
				toJobParameters();

		// when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);
		
		// then
		Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
	}
    
//	@Test
//	void testJobEchoMessageFailure() throws Exception {
//		log.info("{}", "Begin testJobEchoMessageFailure...");
//		
//		// given
//		JobParameters parameters = new JobParametersBuilder().
//				addLong("random", secureRandom.nextLong()).
//				toJobParameters();
//
//		// when
//		JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);
//		
//		// then
//		Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
//	}
}
