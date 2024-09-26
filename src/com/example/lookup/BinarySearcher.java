package com.example.lookup;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

import com.example.constants.Constants;
import com.example.utils.Utils;

public class BinarySearcher {

	public static boolean isPresent(String targetString, String dataFilePath) throws IOException {
		String indexFilePath = Utils.getIndexFilePath(dataFilePath);
		byte[] targetStrPrefixBytes = Utils.getFixedSizePrefixBytes(targetString, Constants.PREFIX_LENGTH);

		try (RandomAccessFile indexFile = new RandomAccessFile(indexFilePath, Constants.READ_MODE);
				RandomAccessFile dataFile = new RandomAccessFile(dataFilePath, Constants.READ_MODE)) {

			long low = 0;
			long high = (indexFile.length() / Constants.INDEX_RECORD_SIZE) - 1;
			long foundIndex = -1;

			while (low <= high) {
				long mid = low + ((high - low) / 2);
				long midIndexPosition = mid * Constants.INDEX_RECORD_SIZE;
				indexFile.seek(midIndexPosition);

				byte[] prefixBytes = new byte[Constants.PREFIX_LENGTH];
				indexFile.readFully(prefixBytes);

				int cmp = Utils.comparePrefixes(targetStrPrefixBytes, prefixBytes);

				if (cmp > 0) {
					low = mid + 1;
				} else if (cmp < 0) {
					high = mid - 1;
				} else {
					foundIndex = mid;
					break;
				}
			}

			if (foundIndex == -1) {
				return false;
			}

			long firstIndex = findFirstOccurrence(indexFile, targetStrPrefixBytes, low, foundIndex);
			long lastIndex = findLastOccurrence(indexFile, targetStrPrefixBytes, foundIndex, high);

			for (long i = firstIndex; i <= lastIndex; i++) {
				long indexPosition = i * Constants.INDEX_RECORD_SIZE;
				indexFile.seek(indexPosition + Constants.PREFIX_LENGTH);

				long offset = indexFile.readLong();

				dataFile.seek(offset);
				int storedLength = dataFile.readInt();
				byte[] stringBytes = new byte[storedLength];
				dataFile.readFully(stringBytes);
				String actualString = new String(stringBytes, StandardCharsets.UTF_8);

				if (actualString.equals(targetString)) {

					return true;
				}
			}
		}

		return false;
	}

	private static long findFirstOccurrence(RandomAccessFile indexFile, byte[] targetPrefixBytes, long low,
			long foundIndex) throws IOException {
		long firstIndex = foundIndex;
		while (firstIndex > low) {
			long previousIndex = firstIndex - 1;
			long indexPosition = previousIndex * Constants.INDEX_RECORD_SIZE;
			indexFile.seek(indexPosition);

			byte[] prefixBytes = new byte[Constants.PREFIX_LENGTH];
			indexFile.readFully(prefixBytes);

			int cmp = Utils.comparePrefixes(targetPrefixBytes, prefixBytes);
			if (cmp == 0) {
				firstIndex = previousIndex;
			} else {
				break;
			}
		}
		return firstIndex;
	}

	private static long findLastOccurrence(RandomAccessFile indexFile, byte[] targetPrefixBytes, long foundIndex,
			long high) throws IOException {
		long lastIndex = foundIndex;
		long maxIndex = (indexFile.length() / Constants.INDEX_RECORD_SIZE) - 1;
		while (lastIndex < Math.min(high, maxIndex)) {
			long nextIndex = lastIndex + 1;
			long indexPosition = nextIndex * Constants.INDEX_RECORD_SIZE;
			indexFile.seek(indexPosition);

			byte[] prefixBytes = new byte[Constants.PREFIX_LENGTH];
			indexFile.readFully(prefixBytes);

			int cmp = Utils.comparePrefixes(targetPrefixBytes, prefixBytes);
			if (cmp == 0) {
				lastIndex = nextIndex;
			} else {
				break;
			}
		}
		return lastIndex;
	}
}
