package passOne;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;

public class PassOne {

	public static void main(String[] args) {
		ArrayList<CodeLine> sicFile = readSICFile(new File("C:\\Users\\micha_000\\Documents\\Programs\\Project3\\Testfile.txt"));
		ObjectTable opLines = readSICOPS(new File("C:\\Users\\micha_000\\Documents\\Programs\\Project3\\SICOPS.txt"));
		sicFile = calculateLocations(sicFile, opLines);
	}
	
	/**
		* Reads through sic file line by line and creates a CodeLine object for each line
		* Adds each CodeLine to an ArrayList and returns the ArrayList
		*/
	public static ArrayList<CodeLine> readSICFile(File file) {
		ArrayList<CodeLine> sicFile = new ArrayList<CodeLine>();
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				String label = "";
				boolean plus = false;
				String operation = "";
				String option = "";
				String operand = "";
				String comment = "";

				String line = sc.nextLine();
				int length = line.length();

				if (line.trim().substring(0,1).equals(".")) {
					comment = line.trim().substring(0);
				} else {
					label = line.substring(0,8);

					if (length > 10) {
						if (line.substring(9, 10).contains("+")) {
							plus = true;
							if (length <= 17) {
								operation = line.substring(9);
							} else {
								operation = line.substring(9,17);
							}
						} else {
							if (length <= 17) {
								operation = line.substring(10);
							} else {
								operation = line.substring(10,17);
							}
						}
					}
					if (length > 18) {
						option = line.substring(18,19);
					}
					if (length > 19){
						if (length <= 29) {
							operand = line.substring(19);
						} else {
							operand = line.substring(19,29);
						}
					} 
					if (length > 31) {
						comment = line.substring(31);
					}

					label = label.trim();
					operation = operation.trim();
					option = option.trim();
					operand = operand.trim();
					comment = comment.trim();

					CodeLine cLine = new CodeLine(label, plus, operation, option, operand, comment);
					for (int i = 0; i < sicFile.size(); i++) {
						CodeLine comp = sicFile.get(i);
						if (comp.label.equals(cLine.label)) {
							cLine.error = "ERROR:     Line " + (i+1) + " duplicate label: " + comp.label + "\n";
						}
					}
					sicFile.add(cLine);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		return sicFile;
	}

	/**
		* Reads through sicops file line by line and creates an Operation object for each line
		* Puts each operation in the ObjectTable (HashTable)
		*/
	public static ObjectTable readSICOPS(File file) {
		ObjectTable ops = new ObjectTable(229);
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				String operation = "";
				String value = "";
				int format = 0;

				String line = sc.nextLine();
				int length = line.length();

				if (length > 6) {
					operation = line.substring(0,8).trim();
					value = line.substring(8,10);
					format = Integer.valueOf(line.substring(14,15));
					Operation oLine = new Operation(operation, value, format);
					System.out.println(ops.put(oLine));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ops;
	}

	/**
		* Goes through each item in the arraylist containing lines of code
		* Examines the format of each operation
		*/
	public static ArrayList<CodeLine> calculateLocations(ArrayList<CodeLine> sicFile, ObjectTable table) {
		int pc = 256; // 100 in hex
		for(int i = 0; i < sicFile.size(); i++) {
			sicFile.get(i).location = pc;
			String op = sicFile.get(i).operation;
			if (op.equals("WORD")) {
				pc += 3;
			} else if (op.equals("RESW")){
				pc += 3*Integer.valueOf(sicFile.get(i).operand);
			} else {
				Operation curOp = table.find(op);
				if (curOp != null) {
					pc += curOp.format;
				}
			}
		}
		for(int i = 0; i < sicFile.size(); i++) {
			sicFile.get(i).printStr();
		}

		return sicFile;
	}

}

class Operation {
	public String operation;
	public String value;
	public int format;

	public Operation(String operation, String value, int format) {
		this.operation = operation;
		this.value = value;
		this.format = format;
	}

	public void printStr() {
		System.out.println(operation + "," + value + "," + format);
	}
}

class CodeLine {
	public String label;
	public boolean plus;
	public String operation;
	public String option;
	public String operand;
	public String comment;
	public int location;
	public String error;
	
	public CodeLine(String label, boolean plus, String operation, String option, String operand, String comment) {
		this.label = label;
		this.plus = plus;
		this.operation = operation;
		this.option = option;
		this.operand = operand;
		this.comment = comment;
	}

	public void printStr() {
		if(error != null) {
			System.out.println(error);
		} else {
			System.out.println(Integer.toHexString(location) + "," + label + "," + plus + "," + operation + "," + option + "," + operand + "," + comment);
		}
	}
}

/**
	* HashTable that stores Operation objects based on operation value
	* 
	*/

class ObjectTable {
	Operation[] hashTable;
	int arraySize;

	//Constructor
	public ObjectTable(int size) {
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
		System.out.println(hash);
		if (hashTable[hash] != null) {
			hashTable[hash].printStr();
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
