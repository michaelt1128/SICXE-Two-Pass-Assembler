package passOne;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PassOne {

	public static void main(String[] args) {
		ArrayList<CodeLine> sicFile = readSICFile(new File(args[0]));
		SymbolTable opLines = readSICOPS(new File(args[1]));
		sicFile = calculateLocations(sicFile, opLines);
		printLocations(sicFile);
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
		* Puts each operation in the SymbolTable (HashTable)
		*/
	public static SymbolTable readSICOPS(File file) {
		SymbolTable ops = new SymbolTable(229);
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
					ops.put(oLine);
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
	public static ArrayList<CodeLine> calculateLocations(ArrayList<CodeLine> sicFile, SymbolTable table) {
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
		return sicFile;
	}

	public static void printLocations(ArrayList<CodeLine> sicFile) {
		System.out.println("****************************************************");
		System.out.println("* Michael Turner Pass One                          *");
		System.out.println("*                                                  *");
		System.out.println("****************************************************");
		System.out.println("Line - Address - Instruction");

		for(int i = 0; i < sicFile.size(); i++) {
			CodeLine line = sicFile.get(i);
			String format = String.format("%%0%dd", 4);
			String result = String.format(format, i);
			System.out.println(result + " - " + line.getHexLocation() + " - " + line.getInstruction());
		}
	}

}

