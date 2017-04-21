package twoPassAssembler;

import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
	* HashTable that stores Operation objects based on operation value
	*
	*/

class SymbolTable {
	Operation[] hashTable;
	int arraySize;

	//Constructor
	public SymbolTable(File file) {
		arraySize = 229;
		hashTable = new Operation[arraySize];
		Arrays.fill(hashTable, null);
		readSICOPS(file);
	}

	//Finds the hash value of the string by:
	// Multiplying each character by 31
	// adding the previous hash value
	// modding it by the array size
	// and repeating for every character in the string
	public int hashFunction(String str) {
		int hash = 0;

		char[] ch = str.toCharArray();
		for (char c : ch) {
			hash = (31 * c + hash) % arraySize;
		}

		return hash;
	}

	//Place a string in the hash table
	public String put(Operation op) {
		int hash = hashFunction(op.operation);
		if (hashTable[hash] == null) {
			hashTable[hash] = op;
			return "Placed " + op.operation + " at location " + hash;
		} else {
			while (hashTable[hash] != null) {
				if (hashTable[hash].operation.equals(op.operation)) {
					return "ERROR: " + op.operation + " already exists at position " + hash;
				}
				hash = (hash + 1) % arraySize;
			}
			hashTable[hash] = op;
			return "Placed " + op.operation + " at location " + hash;
		}
	}

	//Find the location of a string in the hash table
	public Operation find(String op) {
		int hash = hashFunction(op);
		if (hashTable[hash] != null) {
			while (true) {
				String result = hashTable[hash].operation;
				if (result.equals(op)) {
					return hashTable[hash];
				} else {
					hash = (hash + 1) % arraySize;
					if (hashTable[hash] == null) {
						return null;
					}
				}
			}
		} else {
			return null;
		}
	}

	public void readSICOPS(File file) {
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String operation = "";
				String value = "";
				int format = 0;

				String line = sc.nextLine();
				int length = line.length();

				if (length > 6) {
					operation = line.substring(0, 8).trim();
					value = line.substring(8, 10);
					format = Integer.valueOf(line.substring(14, 15));
					Operation oLine = new Operation(operation, value, format);
					this.put(oLine);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
