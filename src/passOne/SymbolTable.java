package passOne;

import java.util.Arrays;

/**
	* HashTable that stores Operation objects based on operation value
	* 
	*/

class SymbolTable {
	Operation[] hashTable;
	int arraySize;

	//Constructor
	public SymbolTable(int size) {
		arraySize = size;
		hashTable = new Operation[size];
		Arrays.fill(hashTable, null);
	}
	
	//Finds the hash value of the string by:
	// Multiplying each character by 31
	// adding the previous hash value
	// modding it by the array size
	// and repeating for every character in the string
	public int hashFunction(String str) {
		int hash = 0;
		
		char[] ch = str.toCharArray();
		for(char c : ch) {
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
			while(hashTable[hash] != null) {
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
			String result = hashTable[hash].operation;
			while (true) {
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
}
