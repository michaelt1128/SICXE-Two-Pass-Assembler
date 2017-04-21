package twoPassAssembler;


public class CodeLine {
	public String label;
	public boolean plus;
	public String operation;
	public String option;
	public String operand;
	public String comment;
	public int location;
	public int size;
	public String opCode;
	public String error;
	
	public CodeLine(String label, boolean plus, String operation, String option, String operand, String comment) {
		this.label = label;
		this.plus = plus;
		this.operation = operation;
		this.option = option;
		this.operand = operand;
		this.comment = comment;
	}

	public String getInstruction() {
		return new String(label + " " + operation + " " + option + operand + " " + comment);
	}

	public String getHexLocation() {
		return String.format("%1$7s", Integer.toHexString(location).toUpperCase());
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public void printStr() {
		if(error != null) {
			System.out.println(error);
		} else {
			System.out.println(Integer.toHexString(location) + "," + label + "," + plus + "," + operation + "," + option + "," + operand + "," + comment);
		}
	}
}
