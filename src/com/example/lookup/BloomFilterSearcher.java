package com.example.lookup;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class BloomFilterSearcher {

	private static BloomFilter<byte[]> bloomFilter;

	public BloomFilterSearcher(long expectedInsertions) {
		if (expectedInsertions < 1) {
			throw new IllegalArgumentException("Invalid value for expected insertions");
		}

		bloomFilter = BloomFilter.create(Funnels.byteArrayFunnel(), expectedInsertions, 0.01);
	}

	public boolean add(byte[] searchTermBytes) {
		Objects.requireNonNull(bloomFilter, "Bloom Filter is not initialized");

		return bloomFilter.put(searchTermBytes);
	}

	public boolean mightContain(String searchTerm) {
		Objects.requireNonNull(bloomFilter, "Bloom Filter is not initialized");

		return bloomFilter.mightContain(searchTerm.getBytes(StandardCharsets.UTF_8));
	}
}
