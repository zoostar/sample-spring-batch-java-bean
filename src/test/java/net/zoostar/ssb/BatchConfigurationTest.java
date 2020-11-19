package net.zoostar.ssb;

import java.security.SecureRandom;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestBatchConfiguration.class, BatchConfiguration.class })
public class BatchConfigurationTest {
	
	private SecureRandom secureRandom = new SecureRandom();
	
	@Autowired
	JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    JobRepositoryTestUtils jobRepositoryTestUtils;
    
    @Rule
    public TestName test = new TestName();
  
    @Before
    public void setup() {
    	log.info("Executing test: {}...", test.getMethodName());
    }
    
    @After
    public void cleanup() {
    	jobRepositoryTestUtils.removeJobExecutions();
    }
    
	@Test
	public void testSuccessfulJobEchoMessage() throws Exception {
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
    
	@Test
	public void testJobEchoMessagewithMissingResource() throws Exception {
		// given
		JobParameters parameters = new JobParametersBuilder().
				addLong("random", secureRandom.nextLong()).
				toJobParameters();

		// when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);
		
		// then
		Assert.assertEquals(ExitStatus.FAILED.getExitCode(), jobExecution.getExitStatus().getExitCode());
	}
    
	@Test
	public void testJobEchoMessagewithInvalidResourcePath() throws Exception {
		// given
		JobParameters parameters = new JobParametersBuilder().
				addLong("random", secureRandom.nextLong()).
				addString("resource", "input.txt").
			toJobParameters();

		// when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);
		
		// then
		Assert.assertEquals(ExitStatus.FAILED.getExitCode(), jobExecution.getExitStatus().getExitCode());
	}
    
	@Test
	public void testJobEchoMessagewithProcessingFailure() throws Exception {
		// given
		JobParameters parameters = new JobParametersBuilder().
				addLong("random", secureRandom.nextLong()).
				addString("resource", "data/invalid_input.txt").
			toJobParameters();

		// when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);
		
		// then
		Assert.assertEquals(ExitStatus.FAILED.getExitCode(), jobExecution.getExitStatus().getExitCode());
	}

}
