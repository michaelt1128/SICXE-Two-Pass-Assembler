package twoPassAssembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;

public class SicFile {
	public ArrayList<CodeLine> sicFile;
	public SymbolTable table;

	public SicFile(File file, SymbolTable table) {
		this.table = table;
		readSICFile(file);
		calculateLocations();
		calculateOpCodes();
	}

	private void readSICFile(File file) {
		sicFile = new ArrayList<CodeLine>();
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String label = "";
				boolean plus = false;
				String operation = "";
				String option = "";
				String operand = "";
				String comment = "";

				String line = sc.nextLine();
				int length = line.length();

				if (line.trim().substring(0, 1).equals(".")) {
					comment = line.trim().substring(0);
				} else {
					label = line.substring(0, 8);

					if (length > 10) {
						if (line.substring(9, 10).contains("+")) {
							plus = true;
							if (length <= 17) {
								operation = line.substring(9);
							} else {
								operation = line.substring(9, 17);
							}
						} else {
							if (length <= 17) {
								operation = line.substring(10);
							} else {
								operation = line.substring(10, 17);
							}
						}
					}
					if (length > 18) {
						option = line.substring(18, 19);
					}
					if (length > 19) {
						if (length <= 29) {
							operand = line.substring(19);
						} else {
							operand = line.substring(19, 29);
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
							cLine.error = "ERROR:     Line " + (i + 1) + " duplicate label: " + comp.label + "\n";
						}
					}
					sicFile.add(cLine);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void calculateLocations() {
		int pc = 256; // 100 in hex
		for (int i = 0; i < sicFile.size(); i++) {
			sicFile.get(i).location = pc;
			String op = sicFile.get(i).operation;
			if (op.equals("WORD")) {
				pc += 3;
				sicFile.get(i).setSize(3);
			} else if (op.equals("RESW")) {
				pc += 3 * Integer.valueOf(sicFile.get(i).operand);
				sicFile.get(i).setSize(3 * Integer.valueOf(sicFile.get(i).operand));
			} else {
				Operation curOp = table.find(op);
				if (curOp != null) {
					pc += curOp.format;
					sicFile.get(i).setSize(curOp.format);
				}
			}
		}
	}

	private void calculateOpCodes() {
		for (int i = 0; i < sicFile.size(); i++) {
			CodeLine line = sicFile.get(i);
			String opCode = "";
			int base = 0;
			int pc = line.location + line.size;
			switch (line.operation) {
			case "WORD":
				String unpadded = Integer.toHexString(Integer.valueOf(line.operand));
				String padded = "000000".substring(unpadded.length()) + unpadded;
				sicFile.get(i).setOpCode(padded);
				break;
			case "RESW":
				sicFile.get(i).setOpCode("");
				break;
			case "BASE":
				// Set the base value equal to the value at the operand
				base = findLabelLocation(line.operand);
			default:
				// Find the operation object from the symbol table
				Operation op = table.find(line.operation);
				if (op != null) {
					if (op.format == 2) {
						char[] registers = line.operand.toCharArray();

					} else {
						int[] nixbpe = new int[6];

						if (line.option.equals("#")) {
							nixbpe[1] = 1;
						} else if (line.option.equals("@")) {
							nixbpe[0] = 1;
						} else {
							nixbpe[0] = 1;
							nixbpe[1] = 1;
						}
						int first2 = Integer.parseInt(op.value, 16) + arrayToDecimal(Arrays.copyOfRange(nixbpe, 0, 2));
						opCode += Integer.toHexString(first2);
						opCode = "00".substring(opCode.length()) + opCode;

						if (line.label.contains(",X")) {
							nixbpe[2] = 1;
						}
						int disp = 0;
						String label = sicFile.get(i).operand;
						int labelLocation = findLabelLocation(label);
						if (line.plus) {
							nixbpe[5] = 1;
							disp = labelLocation;
						} else {
							disp = labelLocation - pc;
							if (disp > 2048 || disp < -2047) {
								nixbpe[3] = 1;
								disp = labelLocation - base;
							} else {
								nixbpe[4] = 1;
							}
						}
						if (first2 == 1) {
							disp = Integer.valueOf(label);
						}
						opCode += Integer.toHexString(arrayToDecimal(Arrays.copyOfRange(nixbpe, 2, 6)));
						System.out.println(Integer.toHexString(arrayToDecimal(Arrays.copyOfRange(nixbpe, 2, 6))));

						if (label.contains(",X")) {
							label = label.substring(0, label.length() - 2);
						}

						String dispStr = String.valueOf(Integer.toHexString(disp));
						if (disp < 0) {
							dispStr = dispStr.substring(5);
						}

						if (sicFile.get(i).plus) {
							dispStr = "00000".substring(dispStr.length()) + dispStr;
						} else {
							dispStr = "000".substring(dispStr.length()) + dispStr;
						}
						System.out.println(dispStr);
						opCode += dispStr;
					}
				}
				sicFile.get(i).setOpCode(opCode);
				break;
			}
		}
	}

	private int arrayToDecimal(int[] array) {
		System.out.println(Arrays.toString(array));
		int multiplier = 1;
		int result = 0;
		for (int i = array.length - 1 ; i >= 0; i--) {
			result += (multiplier * array[i]);
			multiplier *= 2;
		}
		return result;
	}

	private int findLabelLocation(String label) {
		for (int i = 0; i < sicFile.size(); i++) {
			if (sicFile.get(i).label.equals(label)) {
				return sicFile.get(i).location;
			}
		}
		return 0;
	}

}