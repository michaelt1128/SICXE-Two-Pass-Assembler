package twoPassAssembler;

public class Operation {
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