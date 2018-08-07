

public class Token {
	private String type;
	public String value;
	
	private static String operators = "+-/*"; // All operations
	private static String brackets = "()";

	Token(String str) {
	
		
		if (str.equals("(") || str.equals(")")) {
			this.value = str;
			this.type = "bracket";

		} else {
			this.value = str;
			this.type = "operator";
		}
	}

	Token(Double num) {
		this.value = num.toString();
		this.type = "num";
	}

	public String type() {
		return this.type;
	}

	public String op() {
		
		assert (operators.contains(this.value) || brackets.contains(this.value));
		return this.value;
	}
	
	public Double num() {
		Double num = Double.parseDouble(this.value);
		return num;
	}
	
	public void print() {
		System.out.printf("Token: %s, Type: %s, Value: %s\n", this, this.type, this.value);
	}

}