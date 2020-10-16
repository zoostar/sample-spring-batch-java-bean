package net.zoostar.ssb;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

class FileSystemResourceTest {
	
	BatchConfiguration batchConfiguration;
	
	@BeforeEach
	public void setup() {
		batchConfiguration = new BatchConfiguration();
	}

	@Test
	void testFileSystemResource() {
		// Given
		String filename = "input.txt";
		
		// When
		FileSystemResource actual = batchConfiguration.fileSystemResource(filename);
		
		// Then
		Assert.assertEquals(filename, actual.getFilename());
	}

}
