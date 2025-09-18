#include <iostream>
#include <vector>
#include <string>
#include <iomanip>
#include <fstream>

using namespace std;
//forward declaration needed for recursion with P
bool E(const std::string& str);

// base functions, the functions that don't call other functions but are called by other functions
bool D(std::string str){
    std::vector<string> digits={"0","1","2","3","4","5","6","7","8","9"};
    for(int i=0;i< digits.size();i++){
        if (str==digits[i]){
            return true;
        }
    }
    return false;
}

bool C(std::string str){
    std::vector<char> alphabet={'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    char strChar=str.at(0);
    for(int i=0;i< alphabet.size();i++){
        if(strChar==alphabet[i]){
            return true;
        }
    }
    return false;
}

bool U(std::string str){
    if(str=="+" || str=="-" || str=="!"){
        return true;
    }
    return false;
}

bool O(const std::string& str, int precedence) {
    if (precedence == 1) {
        if (str == "+" || str == "-") {
            return true;
        }
    } else if (precedence == 2) {
        if (str == "*" || str == "/") {
            return true;
        }
    } else if (precedence == 3) {
        if (str == "**") {
            return true;
        }
    }
    return false;
}

// complex functions which use the base functions or each other
bool L(std::string str){
        if(str.length()==1){
            if(D(str)){
                return true;
            }
            return false;
        }
        else{
            return L(str.substr(0,1)) && L(str.substr(1)); // passthrough the first character in the string and then the remainder so it can check every character recursively to make sure it is a digit
        }
    }

bool I(std::string str){
        if(str.length()==1){
            if(C(str)){
                return true;
            }
            return false;
        }
        else{
            return I(str.substr(0,1)) && I(str.substr(1)); // passthrough the first character in the string and then the remainder so it can check every character recursively to make sure it is a letter
        }
    }

bool P(const std::string& str) {
    if (str.empty()) {
        return false;
    }

    if (I(str) || L(str)) {
        return true;
    }

    std::string op = str.substr(0, 1);
    if (U(op) && str.length() > 1) {
        std::string strRemaining = str.substr(1);
        return I(strRemaining) || L(strRemaining);
    }

    if (str.front() == '(') {
        int openParenth = 0;
        int matchIndex = -1;
        for (int i = 0; i < str.length(); ++i) {
            char character = str[i];
            if (character == '(') {
                openParenth++;
            } else if (character == ')') {
                openParenth--;
                if (openParenth == 0) {
                    matchIndex = i;
                    break;
                }
            }
        }

        if (matchIndex != -1 && matchIndex == str.length() - 1) {
            std::string expression = str.substr(1, matchIndex - 1);
            return E(expression);
        }
    }

    return false;
}

bool E(const std::string& str) {
    if (P(str)) {
        return true;
    }

    int parenLevel = 0;
    // Iterate backwards to find the lowest precedence operator last
    for (int i = str.length() - 1; i >= 0; --i) {
        char c = str[i];

        if (c == ')') {
            parenLevel++;
        } else if (c == '(') {
            parenLevel--;
        }

        // Only check for operators when not inside parentheses
        if (parenLevel == 0) {
            // Check for double-character operators like '**'
            if (i > 0) {
                std::string potentialOp = str.substr(i - 1, 2);
                if (O(potentialOp, 3)) { // Precedence 3 for exponentiation
                    std::string leftPart = str.substr(0, i - 1);
                    std::string rightPart = str.substr(i + 1);
                    if (P(leftPart) && P(rightPart)) {
                        return true;
                    }
                }
            }

            // Check for single-character operators
            std::string potentialOp = str.substr(i, 1);
            if (O(potentialOp, 1) || O(potentialOp, 2)) { // Precedence 1 or 2
                std::string leftPart = str.substr(0, i);
                std::string rightPart = str.substr(i + 1);
                if (P(leftPart) && P(rightPart)) {
                    return true;
                }
            }
        }
    }
    return false;
}


bool A(std::string str){
        int equalIndex=str.find("=");
        if(equalIndex==-1){// if no equal sign is found the assignment isn't valid in the language
            return false;
        }
        else{
            std::string identifier=str.substr(0,equalIndex); // the identifier is before the equal sign according to the grammar 
            std::string expression=str.substr(equalIndex+1); // the expression is after the equal sign according to the grammar
            if(I(identifier) && E(expression)){ // checks to see if the identifier and expression extracted are valid in the grammar
                return true;
            }
        }
        return false;
    }

void parser(std::string str){
    if(A(str)){
        std::cout << "The string \""+str+"\" is in the language \n";
    }
    else{
        std::cout << "The string \""+str+"\" is not in the language \n";
    }

}

int main(){
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
   

    std::ifstream file("input.txt");
    std::string str;
    if(file.is_open()){
        while(std::getline(file,str)){
            parser(str);
        }
        file.close();
    }
    else{
        std::cerr << "Unable to open the file." << std::endl;
    }


    return 0;
}