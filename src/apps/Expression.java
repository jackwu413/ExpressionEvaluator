package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {
	String expr;
	ArrayList<ScalarSymbol> scalars;
	ArrayList<ArraySymbol> arrays;
    
   
    public static final String delims = " \t*+-/()[]";
   
    public Expression(String expr) {
        this.expr = expr;
    }

    
    public void buildSymbols() {
    	
	    	scalars = new ArrayList<ScalarSymbol>();
	    	arrays = new ArrayList<ArraySymbol>();
	    	
	    
	    StringTokenizer st = new StringTokenizer(expr, delims);
	    
	    
	    while (st.hasMoreTokens() == true) {
	    		boolean isArray = false;
	    		String symbol = st.nextToken();
	    		int len = symbol.length();
	    		try{
	    			
	    			Integer.parseInt(symbol);
	    			
		    	} catch (NumberFormatException e) {
		    		if (expr.indexOf(symbol)+len < expr.length()) {
			    			if (expr.charAt(expr.indexOf(symbol)+len) == '[') {
			    				isArray = true;
			    			}
			    	
		    		}	
		    		if (isArray == true) {
		    			ArraySymbol as = new ArraySymbol(symbol);
		    			if (arrays.contains(as)) {
		    				continue;
		    			} else {
		    				arrays.add(as);
		    			}
		    		} else {
		    			ScalarSymbol ss = new ScalarSymbol(symbol);
		    			if (scalars.contains(ss)) {
		    				continue;
		    			} else {
		    				scalars.add(ss);
		    			}
		    			
		    		}
			    		
		    	} // end try-catch block
	    		
	    }
	    printScalars();
		printArrays();
    }
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    		
    		return recEval(expr);
    		
    	
    }
    	
    
    
    
    private float recEval(String expr) {
    	
    
    	while (expr.indexOf('(') != -1) {
    		String sub = expr.substring(expr.lastIndexOf('(')); //creates a substring from the last ( to the end of the string
    		String s1 = expr.substring(0, expr.lastIndexOf('('));
    		String s2 = sub.substring(sub.indexOf(')')+1);
    		float answer = recEval(sub.substring(1, sub.indexOf(')'))); // call recursive evaluate on everything between last ( ) 
    		expr = s1+answer+s2;
    		expr = expr.replaceAll("--","+");
    	}
    	
    	Stack<Float> nums = new Stack<Float>(); //creates the stack of operands
    	Stack<Character> oper = new Stack<Character>(); //creates the stack of operations
    	StringTokenizer token = new StringTokenizer(expr, delims); //token the expression with appropriate delimiters
    	String ptr; 	
   
    	
    	for (int i = 0; i < expr.length(); i++) {
    		if (expr.charAt(i) == '+' || expr.charAt(i) == '-' || expr.charAt(i) == '*' || expr.charAt(i) == '/') {
    			oper.push(expr.charAt(i));
    		}
    	}
    	
    	while (token.hasMoreTokens() == true) {
    		
    		ptr = token.nextToken();
    		
    		
    		if (Character.isDigit(ptr.charAt(0))) {
    			
    			Float number = Float.parseFloat(ptr);
    			
    				nums.push(number);
    			
    		} else if (Character.isLetter(ptr.charAt(0))) {
    			
    	    		boolean isArray = false;
    	    		String symbol = ptr;
    	    		int len = symbol.length();
    	    		
    		    		if (expr.indexOf(symbol)+len < expr.length()) {
    			    			if (expr.charAt(expr.indexOf(symbol)+len) == '[') {
    			    				isArray = true;
    			    				
    			    			}
    		    		}			
    			    	
    		    		if (isArray == false) {
    		    			for (int i = 0; i < scalars.size(); i++) {
    		    				if (scalars.get(i).name.equals(ptr)) {
    		    					int x = scalars.get(i).value;
    		    					float ss = x;
    		    					nums.push(ss);
    		    				}
    		    			} 
    		    		} 
    		    		if (isArray == true) {	
    		    			for (int i = 0; i< arrays.size(); i++) {
    		    				if (arrays.get(i).name.equals(ptr)) {
    		    					ptr = token.nextToken();
    		    					String tempsubstring = expr.substring(expr.lastIndexOf('['));
    		    					String operand = tempsubstring.substring(1,tempsubstring.indexOf(']'));
    		    					System.out.println("this is the operand:" + operand);
    		    					int temp = (int) recEval(operand);
    		    					System.out.println(temp);
    		    					int[] x = arrays.get(i).values;
    		    					int y = x[temp];
    		    					float z = y;
    		    					String s1 = "";
    		    					for (int var = 0; var < expr.lastIndexOf('['); var++) {
    		    						if (Character.isLetter(expr.charAt(var)) == false) {
    		    							s1 = expr.substring(0, var+1);
    		    							
    		    						}
    		    					}
    		    					
    		    					String s2 = tempsubstring.substring(tempsubstring.indexOf(']') + 1);
    		    					
    		    					
    		    					expr = s1+z+s2;
    		    					System.out.println("this is s1:" + s1);
    		    					System.out.println("this is s2:" + z);
    		    					System.out.println("this is s2:" + s2);
    		    					System.out.println("this is the new expression:" + expr);
    		    					nums.push(z);
    		    					return recEval(expr);
    		    				}
    		    			} //end for loop
    		    		}
    			    		
    		    	} //end if token is scalar or array (not a number)
    	    		
    	    } // end while loop
	if (oper.size() == 0) {
		return nums.peek();
	}

	
    	//IF YOU GET A SINGLE NEGATIVE NUMBER RESULT
	
	if (oper.peek() == '-' && nums.size() ==1) {
		expr = "0"+expr;
		return recEval(expr);
	}
	
	//IF YOU END UP OPERATING ON TWO NEGATIVE NUMBERS
	
	if (nums.size() == 2 && oper.size() == 3) {
		char op1 = oper.pop();
		char op2 = oper.pop();
		if(op2 == '+') {
			float num2 = nums.pop();
			float num1 = nums.pop();
			return (num1+num2)*(-1);
		}
		if(op2 == '-') {
			float num2 = nums.pop();
			float num1 = nums.pop();
			if(num1 < num2) {
				return num2 - num1;
			} else if (num1 > num2) {
				return (num1-num2)*(-1);
			} else { 
				return 0;
			}
		}
		if(op2 == '*') {
			float num2 = nums.pop();
			float num1 = nums.pop();
			return num1*num2;
		}
		if(op2 == '/') {
			float num2 = nums.pop();
			float num1 = nums.pop();
			return num1/num2;
		}
	}
	
	
	
	
	
    	float num2 = 0;
    	float num1 = 0;
    	float temp = 0;
    	Stack<Float> numsholder = new Stack<Float>();
    	Stack<Character> operholder = new Stack<Character>();
  
    	
    	while (nums.isEmpty() == false) {
    		numsholder.push(nums.pop());
    	}
    	
    	while (oper.isEmpty() == false) {
    		operholder.push(oper.pop());
    	}
    	
    	while (operholder.size() > 1) {
    	
	    	char op1 = operholder.pop();
	    	char op2 = operholder.pop();
	    	
	    	if (op1 == '-' && op2 == '/') {
	    		temp = numsholder.pop();
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 / num1);
	    		numsholder.push(temp);
	    		operholder.push(op1);
	    	}
	    	if (op1 == '-' && op2 == '*') {
	    		temp = numsholder.pop();
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 * num1);
	    		numsholder.push(temp);
	    		operholder.push(op1);
	    	}
	    	if (op1 == '+' && op2 == '/') {
	    		temp = numsholder.pop();
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 / num1);
	    		numsholder.push(temp);
	    		operholder.push(op1);
	    	}
	    	if (op1 == '+' && op2 == '*') {
	    		temp = numsholder.pop();
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 * num1);
	    		numsholder.push(temp);
	    		operholder.push(op1);
	    	}
	    	if (op1 == '+' && op2 == '+') {
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 + num1);
	    		operholder.push(op1);
	    	}
	    	if (op1 == '-' && op2 == '-') {
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 - num1);
	    		operholder.push(op1);
	    	}
	    	if (op1 == '*' && op2 == '+') {
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 * num1);
	    		operholder.push(op2);
	    	}
	    	if (op1 == '*' && op2 == '-') {
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 * num1);
	    		operholder.push(op2);
	    	}
	    	if (op1 == '/' && op2 == '+') {
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 / num1);
	    		operholder.push(op2);
	    	}
	    	if (op1 == '/' && op2 == '-') {
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 / num1);
	    		operholder.push(op2);
	    	}
	    if (op1 == '/' && op2 == '/') {
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 / num1);
	    		operholder.push(op1);
	    	}
	    if (op1 == '*' && op2 == '*') {
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num1 * num2);
	    		operholder.push(op2);
	    }
	    if (op1 == '*' && op2 == '/') {
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num1 * num2);
	    		operholder.push(op2);
	    }
	    if (op1 == '/' && op2 == '*') {
		    	num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num1 / num2);
	    		operholder.push(op2);
	    }
	    if (op1 == '+' && op2 == '-') {
	    		num2 = numsholder.pop();
	    		num1 = numsholder.pop();
	    		numsholder.push(num2 + num1);
	    		operholder.push(op2);
	    }
	    if (op1 == '-' && op2 == '+') {
    			num2 = numsholder.pop();
    			num1 = numsholder.pop();
    			numsholder.push(num2 - num1);
    			operholder.push(op2);
	    }
    	}
    	oper.push(operholder.pop());
    	nums.push(numsholder.pop());
    	nums.push(numsholder.pop());
    
	    while (oper.size() == 1) {
		   
	    			if (oper.peek() == '+') {
	    				
	    	    			num2 = nums.pop();
	    	    			num1 = nums.pop();
	    	    		
	    	    			nums.push(num1 + num2);	
	    	    			oper.pop();
	    			}
	    			
	    			else if (oper.peek() == '-') {
	    	    			
	    	    			num2 = nums.pop();
	    	    			num1 = nums.pop();
	    	    		
	    	    			nums.push(num1 - num2);
	    	    			oper.pop();
	    	    		} 
	    		
	    			else if (oper.peek() == '*') {
	    	    			
	    	    			num2 = nums.pop();
	    	    			num1 = nums.pop();
	    	    			
	    	    			nums.push(num1 * num2);
	    	    			oper.pop();
	    	    		} 
	    			
	    			else if (oper.peek() == '/') {
	    	    			
	    	    			num2 = nums.pop();
	    	    			num1 = nums.pop();
	    	    		
	    	    			nums.push(num1 / num2);
	    	    			oper.pop();
	    			}
	    		}
	    
	    
	    return nums.peek();
    }


    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
