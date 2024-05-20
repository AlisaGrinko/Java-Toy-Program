import java.util.*;
import java.util.regex.*;

class Toy {
    static String inputProgram;
    static int inputIndex;
    static char inputToken;
    static final String IDENTIFIER = "[a-zA-Z_][a-zA-Z0-9_]*";
    static final String LITERAL = "0|[1-9][0-9]*";
    static Map<String, Integer> variablesWithValues = new HashMap<>();

    public static void main(String[] args){
	if (args.length != 1) {
	    System.out.println("Usage example: java Toy \"x = 1; y = 2; z = ---(x+y)*(x+-y);\"");
	    return;
	}
	inputProgram = args[0];
	inputIndex = 0;
	nextToken();
	try {
	    parseInputProgram();	    
		for (String varName : variablesWithValues.keySet()) {
		    System.out.println(varName + " = " + variablesWithValues.get(varName));
		}
	} catch (RuntimeException e){
	    System.out.println("error");
	    System.exit(1);
	}
	     
    }

    static void nextToken(){
	while (inputIndex < inputProgram.length() && Character.isWhitespace(inputProgram.charAt(inputIndex))){
	    inputIndex++;
	}
	if (inputIndex <  inputProgram.length()) {
	    inputToken = inputProgram.charAt(inputIndex);
	    inputIndex++;
	} else {
	    inputToken = '$';
	}
    }
    static void parseInputProgram(){
	while(inputToken != '$'){
	    parseSingleAssignment();
	}
    }
    static void parseSingleAssignment(){
	String identifier = parseIdentifier();
	match('=');
	Integer value = parseExp();
	match(';');
	variablesWithValues.put(identifier, value);	
    }
    static String parseIdentifier(){
	StringBuilder identifier = new StringBuilder();
	if (Character.isLetter(inputToken) || inputToken == '_') {
	    identifier.append(inputToken);
	    nextToken();
	    while (Character.isLetterOrDigit(inputToken) || inputToken == '_') {
		identifier.append(inputToken);
		nextToken();
	    }
	} else {
	    error("error");
		}
	String parsedIdentifier = identifier.toString();
	if (!Pattern.matches(IDENTIFIER, parsedIdentifier)) {
	    error("error");
	}
	return parsedIdentifier;
    }

    static void  match(char expectedToken){
	if (inputToken == expectedToken){
	    nextToken();
	} else {
	    error("error");
	}
    }

    static Integer parseExp(){
	Integer termValue = parseTerm();
	return parseExpPrime(termValue);
    }

    static Integer parseTerm(){
	Integer factorValue = parseFactor();
	return parseTermPrime(factorValue);
    }

    static Integer parseExpPrime(Integer termValue){
	while (inputToken == '+' || inputToken == '-'){
	    char operator = inputToken;
	    nextToken();
	    Integer nextTerm = parseTerm();
	    if (nextTerm == null || termValue == null){
		return null;
	    }
	    if (operator == '+'){
		termValue += nextTerm;
	    } else {
		termValue -= nextTerm;
	    }
	}
	return termValue;
    }

    static Integer parseTermPrime(Integer factorValue){
	while (inputToken == '*'){
	    nextToken();
	    Integer nextFactor = parseFactor();
	    if (nextFactor == null || factorValue == null){
		return null;
	    }
	    factorValue *= nextFactor;
	}
	return factorValue;
    }
	

    static Integer parseFactor(){
	boolean isNegative = false;
	while(inputToken == '-' || inputToken == '+'){
	    if(inputToken == '-'){
		isNegative = !isNegative;
	    }
	    nextToken();
    }
	Integer factorValue;
	if (inputToken == '('){
	    nextToken();
	    factorValue = parseExp();
	    match(')');
	} else if (Character.isDigit(inputToken)){
	    factorValue = parseLiteral();
	} else if (Character.isLetter(inputToken) || inputToken == '_'){
	    factorValue = parseVariable();
	} else {
	    error("Invalid factor");
	    return null;
	}
	return isNegative ? -factorValue : factorValue;
    }

    static Integer parseLiteral(){
	StringBuilder literal = new StringBuilder();
	while (Character.isDigit(inputToken)){
	    literal.append(inputToken);
	    nextToken();
	}

	String parsedLiteral = literal.toString();
	if(!Pattern.matches(LITERAL, parsedLiteral)){
	    error("error");
	}
	return Integer.parseInt(parsedLiteral);
    }

    static Integer parseVariable(){
	String identifier = parseIdentifier();
	if(!variablesWithValues.containsKey(identifier)){
	    error("error");
	}
	return variablesWithValues.get(identifier);
    }

    static void error(String msg){
        throw new RuntimeException(msg);   
    }
}
