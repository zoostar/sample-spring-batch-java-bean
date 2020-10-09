package net.zoostar.ssb;

import org.junit.After;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
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
@ContextConfiguration(classes = { SampleSpringBatchApplication.class, BatchConfiguration.class })
public class SampleSpringBatchApplicationTest {

	private JobLauncherTestUtils jobLauncherTestUtils; 

    @Autowired
    public void setJobLauncherTestUtils(JobLauncherTestUtils jobLauncherTestUtils) {
    	log.debug("setJobLauncherTestUtils({})", jobLauncherTestUtils);
    	this.jobLauncherTestUtils = jobLauncherTestUtils;
    }
//    @Autowired
//    JobRepositoryTestUtils jobRepositoryTestUtils;
    
    @After
    void cleanup() {
//    	jobRepositoryTestUtils.removeJobExecutions();
    }
    
	@Test
	void testJobEchoMessage() throws Exception {
		log.info("{}", "Begin Test...");
        // given
        JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        Assert.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
	}

}
