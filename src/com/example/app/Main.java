package com.example.app;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.example.lookup.BloomFilterSearcher;
import com.example.lookup.FileRecordManager;
import com.example.lookup.Lookup;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class);

	private static final String BINARY_FILE_PATH = System.getProperty("user.home") + "/Documents/data.bin";
	private static final long EXPECTED_INSERTIONS = 10000;

	public static void main(String[] args) {
		BloomFilterSearcher bloomFilterSearcher = new BloomFilterSearcher(EXPECTED_INSERTIONS);
		FileRecordManager fileRecordManager = new FileRecordManager(BINARY_FILE_PATH, bloomFilterSearcher);

		boolean isContinue = true;
		Scanner in = new Scanner(System.in);
		while (isContinue) {
			System.out.println("Select an operation: \n \t\t 1. Search \n \t\t 2. Add \n \t\t 3. Exit");

			int operation = -1;
			try {
				operation = in.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Invalid operation");
				continue;
			}

			switch (operation) {
			case 1:
				handleSearchOperation(in, bloomFilterSearcher);
				break;
			case 2:
				handleAddOperation(in, fileRecordManager);
				break;
			case 3:
				isContinue = false;
				break;
			default:
				System.out.println("Invalid operation");
				break;
			}
		}
	}

	private static void handleSearchOperation(Scanner in, BloomFilterSearcher bloomFilterSearcher) {
		System.out.println("Enter search term");
		try {
			String searchTerm = in.next();
			in.nextLine();
			boolean isPresent;
			isPresent = Lookup.isPresent(searchTerm, BINARY_FILE_PATH, bloomFilterSearcher);
			String message = "present";
			if (!isPresent) {
				message = "not " + message;
			}
			System.out.println("The search term is " + message);
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "IO Exception occured", e);
			System.out.println("Error occured. Please try again");
		}
	}

	private static void handleAddOperation(Scanner in, FileRecordManager fileRecordManager) {
		System.out.println("Enter data to add");
		try {
			String newRecord = in.next();
			in.nextLine();
			fileRecordManager.add(newRecord);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.log(Level.ERROR, "IO Exception occured", e);
			System.out.println("Error occured. Please try again");
		}
	}
}
