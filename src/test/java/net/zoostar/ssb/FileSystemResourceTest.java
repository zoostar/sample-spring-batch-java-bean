package net.zoostar.ssb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

public class FileSystemResourceTest {
	
	BatchConfiguration batchConfiguration;
	
	@Before
	public void setup() {
		batchConfiguration = new BatchConfiguration();
	}

	@Test
	public void testFileSystemResource() {
		// Given
		String filename = "input.txt";
		
		// When
		FileSystemResource actual = batchConfiguration.fileSystemResource(filename);
		
		// Then
		Assert.assertEquals(filename, actual.getFilename());
	}

}
