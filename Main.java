import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /*
        Grammar Rules:
            A -> I = E
            E -> P O P | P
            O -> + | - | * | / | **
            P -> I | L | UI | UL | (E)
            U -> + | - | !
            I -> C | CI
            C -> a | b | ... | y | z
            L -> D | DL
            D -> 0 | 1 | ... | 8 | 9
        */
//        File file= new File("src/input.txt"); //for jetbrains/windows use only
        File file= new File("input.txt");
//        File file= new File("src/tests.txt");
        try {
            Scanner scanner= new Scanner(file);
            while(scanner.hasNextLine()){
                String str=scanner.nextLine();
                parser(str);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void parser(String str){
        if(A(str)){
            System.out.println("The string \""+str+"\" is in the language.");
        }
        else{
            System.out.println("The string \""+str+"\" is not in the language.");
        }

    }
    // base functions, the functions that don't call other functions but are called by other functions
    public static boolean D(String str){
        String[] digits={"0","1","2","3","4","5","6","7","8","9"};
        for(int i=0;i< digits.length;i++){
            if (str.equals(digits[i])){
                return true;
            }
        }
        return false;
    }

    public static boolean C(String str){
        char[] alphabet={'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        char strChar=str.charAt(0);
        for(int i=0;i< alphabet.length;i++){
            if(strChar==alphabet[i]){
                return true;
            }
        }
        return false;
    }

    public static boolean U(String str){
        if(str.equals("+") || str.equals("-") || str.equals("!")){
            return true;
        }
        return false;
    }

    public static boolean O(String str, int precedence){
        if(precedence==1){
            if( str.equals("+") || str.equals("-")){
                return true;
            }
        }
        else if(precedence==2){
            if( str.equals("*") || str.equals("/")){
                return true;
            }
        }
        else if(precedence==3){
            if( str.equals("**")){
                return true;
            }
        }
        return false;
    }

    // complex functions which use the base functions or each other
    public static boolean L(String str){
        if(str.length()==1){
            if(D(str)){
                return true;
            }
            return false;
        }
        else{
            return L(str.substring(0,1)) && L(str.substring(1)); // passthrough the first character in the string and then the remainder so it can check every character recursively to make sure it is a digit
        }
    }

    public static boolean I(String str){
        if(str.length()==1){
            if(C(str)){
                return true;
            }
            return false;
        }
        else{
            return I(str.substring(0,1)) && I(str.substring(1)); // passthrough the first character in the string and then the remainder so it can check every character recursively to make sure it is a letter
        }
    }

    public static boolean P(String str){
        if (str == null || str.isEmpty()) {
            return false;
        }
        if(I(str) || L(str)){
            return true;
        }
        String operator=str.substring(0,1);
        if(U(operator) && str.length()>1){
            String strRemaining=str.substring(1);
            return I(strRemaining) || L(strRemaining);
        }
        String firstChar=str.substring(0,1);
        String lastChar=str.substring(str.length()-1);
        if(firstChar.equals("(")){
            int openParenth=0;
            int matchIndex=-1;
            for(int i=0;i<str.length();i++){
                String character=str.substring(i,i+1);
                if(character.equals("(")){
                    openParenth++;
                }
                else if(character.equals(")")){
                    openParenth--;
                    if(openParenth==0){
                        matchIndex=i;
                        break;
                    }
                }
            }
            if(matchIndex==str.length()-1){
                String expression=str.substring(1,matchIndex);
                boolean result=E(expression);
                return result;
            }
        }
        return false;
    }

public static boolean E(String str){
    if(P(str)){
        return true;
    }
    else{
        int parenLevel = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);

            if (c == ')') parenLevel++;
            else if (c == '(') parenLevel--;

            // checks for operators when not inside parentheses
            if (parenLevel == 0) {
                // checks for double operator **
                if (i > 0) {
                    String potentialOp = str.substring(i - 1, i + 1);
                    if (O(potentialOp, 3)) {
                        String leftPart = str.substring(0, i - 1);
                        String rightPart = str.substring(i + 1);
                        if (P(leftPart) && P(rightPart)) {
                            return true;
                        }
                    }
                }
                // checks for single operators
                String potentialOp = str.substring(i, i + 1);
                if (O(potentialOp, 1) || O(potentialOp, 2)) {
                    String leftPart = str.substring(0, i);
                    String rightPart = str.substring(i + 1);
                    if (P(leftPart) && P(rightPart)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

    public static boolean A(String str){
        int equalIndex=str.indexOf("=");
        if(equalIndex==-1){ // if no equal sign is found the assignment isn't valid in the language
            return false;
        }
        else{
            String identifier=str.substring(0,equalIndex); // the identifier is before the equal sign according to the grammar
            String expression=str.substring(equalIndex+1); // the expression is after the equal sign according to the grammar
            if(I(identifier) && E(expression)){ // checks to see if the identifier and expression extracted are valid in the grammar
                return true;
            }
        }
        return false;
    }
}