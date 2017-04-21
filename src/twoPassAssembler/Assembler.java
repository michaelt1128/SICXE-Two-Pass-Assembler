package twoPassAssembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Assembler {

	public static void main(String[] args) {
		SymbolTable opLines = new SymbolTable(new File(args[1]));
		SicFile file = new SicFile(new File(args[0]), opLines);

		printResults(file.sicFile);
	}
	
	public static void printResults(ArrayList<CodeLine> sicFile) {
		System.out.println("****************************************************");
		System.out.println("* Michael Turner SIC-XE Two Pass Assembler         *");
		System.out.println("*                                                  *");
		System.out.println("****************************************************");
		System.out.println("Line - Address -  OpCode  - Instruction");

		for(int i = 0; i < sicFile.size(); i++) {
			CodeLine line = sicFile.get(i);
			
			String format = String.format("%%0%dd", 4);
			String result = String.format(format, i);


			String opCode = String.format("%8s", line.opCode);

			System.out.println(result + " - " + line.getHexLocation() + " - " + opCode + " - " + line.getInstruction());
		}
	}

}

