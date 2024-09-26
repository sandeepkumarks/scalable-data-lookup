package com.example.generator;

import java.io.IOException;

import com.example.lookup.BloomFilterSearcher;
import com.example.lookup.FileRecordManager;
import com.example.utils.Utils;

public class DataSetGenerator {

	private long dataSetGenerationCount;
	private String dataFilePath;
	private int eachDataLength;
	private FileRecordManager fileRecordManager;

	public DataSetGenerator(long dataSetGenerationCount, String dataFilePath, int eachDataLength,
			BloomFilterSearcher bloomFilterSearcher) {
		this.dataSetGenerationCount = dataSetGenerationCount;
		this.dataFilePath = dataFilePath;
		this.eachDataLength = eachDataLength;

		this.fileRecordManager = new FileRecordManager(this.dataFilePath, bloomFilterSearcher);
	}

	public void generate() throws IOException {
		for (long i = 0; i < dataSetGenerationCount; i++) {
			String randomStr = Utils.generateRandomString(eachDataLength);
			fileRecordManager.add(randomStr);
		}
	}
}
