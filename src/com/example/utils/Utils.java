package com.example.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import com.example.constants.Constants;

public class Utils {

	private static final Random RANDOM = new Random();

	public static String getIndexFilePath(String dataFilePath) {
		int pathSeparatorLastIndex = dataFilePath.lastIndexOf(Constants.FILE_SEPARATOR);
		String indexFilePath = dataFilePath.substring(0, pathSeparatorLastIndex + 1) + Constants.INDEX_FILE_NAME;

		return indexFilePath;
	}

	public static byte[] getFixedSizePrefixBytes(String str, int prefixLength) {
		return getFixedSizePrefixBytes(str.getBytes(StandardCharsets.UTF_8), prefixLength);
	}

	public static byte[] getFixedSizePrefixBytes(byte[] strBytes, int prefixLength) {
		byte[] prefixBytes = new byte[prefixLength];

		Arrays.fill(prefixBytes, (byte) 0);

		int copyLength = Math.min(strBytes.length, prefixLength);
		System.arraycopy(strBytes, 0, prefixBytes, 0, copyLength);

		return prefixBytes;
	}

	public static String generateRandomString(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = RANDOM.nextInt(Constants.ALPHA_NUMERIC.length());
			sb.append(Constants.ALPHA_NUMERIC.charAt(index));
		}
		return sb.toString();
	}

	public static int comparePrefixes(byte[] prefix1, byte[] prefix2) {
		for (int i = 0; i < Constants.PREFIX_LENGTH; i++) {
			int b1 = prefix1[i] & 0xFF;
			int b2 = prefix2[i] & 0xFF;
			if (b1 != b2) {
				return b1 - b2;
			}
		}
		return 0;
	}

}
