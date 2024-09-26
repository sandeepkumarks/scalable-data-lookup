package com.example.lookup;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.example.constants.Constants;
import com.example.utils.Utils;

public class FileRecordManager {

	private static final String TEMP_DATA_FILE_NAME = "tempDataFile";

	private String dataFilePath;
	private String indexFilePath;
	private BloomFilterSearcher bloomFilterSearcher;

	public FileRecordManager(String dataFilePath, BloomFilterSearcher bloomFilterSearcher) {
		this.dataFilePath = dataFilePath;
		this.indexFilePath = Utils.getIndexFilePath(dataFilePath);
		this.bloomFilterSearcher = bloomFilterSearcher;
	}

	public void add(String newRecord) throws IOException {
		long insertPosition = getInsertPosition(newRecord);

		File tempDataFile = File.createTempFile(TEMP_DATA_FILE_NAME, Constants.BINARY_FILE_EXTENSION);
		File tempIndexFile = File.createTempFile(TEMP_DATA_FILE_NAME, Constants.BINARY_FILE_EXTENSION);

		tempDataFile.deleteOnExit();
		tempIndexFile.deleteOnExit();

		try (RandomAccessFile indexFile = new RandomAccessFile(indexFilePath, Constants.READ_WRITE_MODE);
				RandomAccessFile dataFile = new RandomAccessFile(dataFilePath, Constants.READ_WRITE_MODE);
				RandomAccessFile tempDataRAF = new RandomAccessFile(tempDataFile, Constants.READ_WRITE_MODE);
				RandomAccessFile tempIndexRAF = new RandomAccessFile(tempIndexFile, Constants.READ_WRITE_MODE)) {

			// Data file update logic (in sorted manner)
			long indexOffsetPosition = (insertPosition * Constants.INDEX_RECORD_SIZE);

			long dataOffset;
			if (insertPosition == 0) {
				dataOffset = 0;
			} else if (indexFile.length() == indexOffsetPosition) {
				dataOffset = dataFile.length();
			} else {
				indexFile.seek(indexOffsetPosition + Constants.PREFIX_LENGTH);
				dataOffset = indexFile.readLong();
			}

			dataFile.seek(dataOffset);
			copyRemainingToTempFile(dataFile, tempDataRAF);
			dataFile.seek(dataOffset);
			insertNewRecord(newRecord, dataFile, indexFile);
			copyBackFromTempFile(tempDataRAF, dataFile);

			rebuildIndexFile(indexFile, dataFile);

		}

		if (tempDataFile.exists()) {
			tempDataFile.delete();
		}
		if (tempIndexFile.exists()) {
			tempIndexFile.delete();
		}
	}

	private long getInsertPosition(String targetStr) throws IOException {
		byte[] targetStrPrefixBytes = Utils.getFixedSizePrefixBytes(targetStr, Constants.PREFIX_LENGTH);

		try (RandomAccessFile indexFile = new RandomAccessFile(indexFilePath, Constants.READ_MODE)) {
			long left = 0;
			long right = (indexFile.length() / Constants.INDEX_RECORD_SIZE) - 1;

			while (left <= right) {
				long mid = left + ((right - left) / 2);
				long midIndexPosition = mid * Constants.INDEX_RECORD_SIZE;
				indexFile.seek(midIndexPosition);

				byte[] prefixBytes = new byte[Constants.PREFIX_LENGTH];
				indexFile.readFully(prefixBytes);

				int comparedResult = Utils.comparePrefixes(targetStrPrefixBytes, prefixBytes);

				if (comparedResult == 0) {
					return mid + 1;
				} else if (comparedResult > 0) {
					left = mid + 1;
				} else {
					right = mid - 1;
				}
			}

			return left;
		}
	}

	private void copyRemainingToTempFile(RandomAccessFile sourceFile, RandomAccessFile tempFile) throws IOException {
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = sourceFile.read(buffer)) != -1) {
			tempFile.write(buffer, 0, bytesRead);
		}
	}

	private void insertNewRecord(String newRecord, RandomAccessFile dataFile, RandomAccessFile indexFile)
			throws IOException {
		byte[] newRecordBytes = newRecord.getBytes();

		dataFile.writeInt(newRecordBytes.length);
		dataFile.write(newRecordBytes);

		this.bloomFilterSearcher.add(newRecordBytes);
	}

	private void copyBackFromTempFile(RandomAccessFile tempFile, RandomAccessFile dataFile) throws IOException {
		tempFile.seek(0);
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = tempFile.read(buffer)) != -1) {
			dataFile.write(buffer, 0, bytesRead);
		}
	}

	private void rebuildIndexFile(RandomAccessFile indexFile, RandomAccessFile dataFile) throws IOException {
		indexFile.setLength(0);

		long currentOffset = 0;

		dataFile.seek(0);

		while (dataFile.getFilePointer() < dataFile.length()) {
			int recordLength = dataFile.readInt();
			byte[] recordBytes = new byte[recordLength];
			dataFile.readFully(recordBytes);

			byte[] prefixBytes = Utils.getFixedSizePrefixBytes(recordBytes, Constants.PREFIX_LENGTH);

			indexFile.write(prefixBytes);
			indexFile.writeLong(currentOffset);

			currentOffset += Constants.FULL_DATA_LENGTH_SIZE + recordLength;
		}
	}
}
