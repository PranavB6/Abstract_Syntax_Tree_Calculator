import java.util.Scanner;
import java.util.ArrayList;

public class Calc {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Program Initialized");

		// ---
		Scanner scanner = new Scanner(System.in);
		String expression = scanner.nextLine().replaceAll("\\s","");
		// ---

		// ---
		ArrayList<Token> tokens = tokenizer("0+" + expression);
		Parser token_manager = new Parser(tokens);
		Node tree = treeBuilder(token_manager, null);
		Double result = evaluate(tree);
		// ---

		System.out.printf("Tree: ");
		tree.print();		
		System.out.println("");
		System.out.printf("Result: %s", result);		

		scanner.close();

	}

	static ArrayList<Token> tokenizer(String elements) {

		String nums = ".0123456789"; // All numbers
		String symbols = "+-/*()"; // All operations
		StringBuilder constant_buffer = new StringBuilder(); // We will build the numbers, one character at a time
		ArrayList<Token> tokens = new ArrayList<Token>(); // Will contain Doubles for numbers, strings for operations

		for (int i = 0; i < elements.length(); ++i) { // Iterate through the input

			String element_s = String.valueOf(elements.charAt(i)); // get one character at a time (but convert each
																	// character to a string)

			if (nums.contains(element_s)) { // If it is a number...
				// Add it to the constant buffer
				constant_buffer.append(element_s);

			} else if (symbols.contains(element_s)) { // If it is an operation...
				

				if (constant_buffer.length() > 0) {
					tokens.add(new Token(Double.parseDouble(constant_buffer.toString()))); // Then we have reached the
																							// end of the number so
																							// convert the number buffer
																							// to a string and then to a
																							// double
					constant_buffer.setLength(0); // Reset the buffer, to get the next number
				}
				tokens.add(new Token(element_s)); // Add the operation to the tokens list

			} else if (element_s == " ") {
				continue;

			} else {
				System.out.println("Invalid Characer");
			}

		
		}

		if (constant_buffer.length() > 0) {
			tokens.add(new Token(Double.parseDouble(constant_buffer.toString()))); // The end of the expression doesn't
																					// have an operation so we need to
																					// manually add the last number
			constant_buffer.setLength(0); // Reset the buffer
		}

		return tokens;
	}
	
	
	static Node treeBuilder(Parser token_manager, Node root) {
		
		Token nextToken = token_manager.nextToken();	// Get the next Token
		Boolean is_next = nextToken != null;			// Make sure the nextToken is an actual token	
		
		if (!is_next) { return root; }					// If it isn't a token, we have gone through all the tokens and now we can return the root
		
		System.out.printf("Current token value: %s\n", nextToken.value);
		
		// If there isn't a root yet...
		if (root == null) {

			// If there isn't a root and the first token is a + or -, then it is the sign of an integer
			// So the next token should be the magnitude of the integer
			// Combine the sign and the magnitude into one number and then turn that into a token
			if (nextToken.op().equals("+") || nextToken.op().equals("-")) {
				nextToken = new Token(Double.parseDouble(((nextToken.op() + token_manager.nextToken().value.toString()))));
			}

			// If there is no root then the next token is the root
			Node newRoot = new Node(nextToken);
			return treeBuilder(token_manager, newRoot);
		}
	
		if (nextToken.type().equals("num")) {
			root.addLeaf(new Node(nextToken));
			return treeBuilder(token_manager, root);
			
		} else if (nextToken.op().equals("+") || nextToken.op().equals("-")) {
			// Add above
			Node newRoot = new Node(nextToken);
			newRoot.leftC = root;
			return treeBuilder(token_manager, newRoot);
			
		} else if (nextToken.op().equals("*") || nextToken.op().equals("/")) {
			// Add below
			Node mulDiv_node = new Node(nextToken);
			mulDiv_node.leftC = root.rightC;
			root.rightC = mulDiv_node;
			return treeBuilder(token_manager, root);
			
		} else if (nextToken.op().equals("(")) {
			
			ArrayList<Token> new_tokens = new ArrayList<Token>();
			int count = 1;
			
			nextToken = token_manager.nextToken();
			if (nextToken.value.equals("(")) {++count;}
			if (nextToken.value.equals(")")) {--count;}
			
			while (count != 0) {
				new_tokens.add(nextToken);
				
				nextToken = token_manager.nextToken();
				if (nextToken.value.equals("(")) {++count;}
				if (nextToken.value.equals(")")) {--count;}
				
				
				
				
			}
			
			Parser bracket_manager = new Parser(new_tokens);
			
			Node bracket_root = treeBuilder(bracket_manager, null);
			
			root.addLeaf(bracket_root);
			return treeBuilder(token_manager, root);
		}
		
	
			
		return root;
		
	}
	
	static Double evaluate(Node root) {
		
		if (root.token.type().equals("num")) {
			return Double.parseDouble(root.token.value);
			
		} else if (root.token.op().equals("+")) {
			return (evaluate(root.leftC) + evaluate(root.rightC));
			
		} else if (root.token.op().equals("-")) {
			return (evaluate(root.leftC) - evaluate(root.rightC));
			
		} else if (root.token.op().equals("*")) {
			return (evaluate(root.leftC) * evaluate(root.rightC));
			
		} else if (root.token.op().equals("/")) {
			return (evaluate(root.leftC) / evaluate(root.rightC));
			
		} else {
			throw new IllegalArgumentException("Invalid Tree"); 
		}
	}
	
	
}

class Node {

	public Token token;
	public Node leftC;
	public Node rightC;
	
	Node(Token t) {
		this.token = t;
	}
	
	public void addLeaf(Node num) {
		
		if (this.token.type() == "num") {
			throw new IllegalArgumentException("Tried to add leaf to leaf"); 
		}
		
		if (this.leftC == null) {
			this.leftC = num;		
			
		} else if (!this.leftC.full()) {
			this.leftC.addLeaf(num);
			
		} else if (this.rightC == null) {
			this.rightC = num;
			
		} else if (!this.rightC.full()) {
			this.rightC.addLeaf(num);
			
		} else {
			throw new IllegalArgumentException("Tried to add leaf but tree is full");
		}
			
	}
	
	public boolean full() {
		if (this.token.type() == "num") { return true; }
		
		if (this.leftC != null && this.rightC != null) {
			if (this.leftC.full() && this.rightC.full()) {
				return true;
			}
			
			else {
				return false;
			}
			
		}
		else { return false; }
	}
	public void print() {
		boolean is_leftC; boolean is_rightC;
		
		System.out.printf("(%s", token.value);
		
		if (is_leftC = this.leftC != null) {
			System.out.printf(", ");
			this.leftC.print();
			
		}
		
		if (is_rightC = this.rightC != null) {
			System.out.printf(", ");
			this.rightC.print();
		} 
		
		System.out.printf(")");
		
	}

}


class Parser {
	
	private ArrayList<Token> tokens;
	private int tokens_n;
	int cursor;
	
	Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
		this.tokens_n = tokens.size();
		this.cursor = -1;
	}
	
	public Token nextToken() {
		++cursor;
		
		if (cursor < tokens_n) {
			return this.tokens.get(this.cursor);
		} else {
			return null;
		}
		
	}
}
