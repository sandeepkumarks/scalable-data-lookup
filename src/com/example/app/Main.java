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
		try {
			BloomFilterSearcher bloomFilterSearcher = new BloomFilterSearcher(EXPECTED_INSERTIONS);
			FileRecordManager fileRecordManager = new FileRecordManager(BINARY_FILE_PATH, bloomFilterSearcher);
			runApplication(bloomFilterSearcher, fileRecordManager);
		} catch (Exception e) {
			LOGGER.log(Level.FATAL, "Unexpected error occurred in the application.", e);
			System.out.println("An unexpected error occurred. Please try again.");
		}
	}

	private static void runApplication(BloomFilterSearcher bloomFilterSearcher, FileRecordManager fileRecordManager) {
		boolean isContinue = true;
		Scanner in = new Scanner(System.in);

		while (isContinue) {
			int operation = getValidIntInput(in,
					"Select an operation: \n \t\t 1. Search \n \t\t 2. Add \n \t\t 3. Exit");
			LOGGER.info("User selected operation: " + operation);

			switch (operation) {
			case 1:
				handleSearchOperation(in, bloomFilterSearcher);
				System.out.println("Search completed.");
				break;
			case 2:
				handleAddOperation(in, fileRecordManager);
				System.out.println("Add operation completed.");
				break;
			case 3:
				isContinue = !confirmExit(in);
				break;
			default:
				System.out.println("Invalid operation. Please select a valid option.");
				break;
			}
		}
	}

	private static int getValidIntInput(Scanner in, String message) {
		int operation = -1;
		while (operation == -1) {
			System.out.println(message);
			try {
				operation = in.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Please enter a valid number.");
				in.nextLine();
			}
		}
		return operation;
	}

	private static boolean confirmExit(Scanner in) {
		System.out.println("Are you sure you want to exit? (Y/N)");
		String confirm = in.next();
		return confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("N");
	}

	private static void handleSearchOperation(Scanner in, BloomFilterSearcher bloomFilterSearcher) {
		System.out.println("Enter search term");
		try {
			String searchTerm = in.next().trim();
			in.nextLine();
			if (searchTerm.isEmpty()) {
				System.out.println("Invalid input. Search term cannot be empty.");
				return;
			}
			boolean isPresent = Lookup.isPresent(searchTerm, BINARY_FILE_PATH, bloomFilterSearcher);
			System.out.println("The search term is " + (isPresent ? "present" : "not present"));
			LOGGER.info("Search term: " + searchTerm + " - Result: " + (isPresent ? "Found" : "Not Found"));
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "IO Exception occurred", e);
			System.out.println("Error occurred. Please try again.");
		}
	}

	private static void handleAddOperation(Scanner in, FileRecordManager fileRecordManager) {
		System.out.println("Enter data to add");
		try {
			String newRecord = in.next().trim();
			in.nextLine();
			if (newRecord.isEmpty()) {
				System.out.println("Invalid input. Data cannot be empty.");
				return;
			}
			fileRecordManager.add(newRecord);
			LOGGER.info("New record added: " + newRecord);
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "IO Exception occurred", e);
			System.out.println("Error occurred. Please try again.");
		}
	}
}
