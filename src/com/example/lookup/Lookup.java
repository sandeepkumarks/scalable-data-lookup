package com.example.lookup;

import java.io.IOException;

public class Lookup {
	public static boolean isPresent(String searchTerm, String binaryFilePath, BloomFilterSearcher bloomFilterSearcher)
			throws IOException {

		boolean isPresentInBloomFilter = bloomFilterSearcher.mightContain(searchTerm);
		if (!isPresentInBloomFilter) {
			return false;
		}

		return BinarySearcher.isPresent(searchTerm, binaryFilePath);
	}
}
