package ch14.code04;

import java.text.ParseException;
import java.util.*;

class Args {
   private String schema;
   private String[] args;
   private boolean valid = true;
   private Set<Character> unexpectedArguments = new TreeSet<>();
   private Map<Character, ArgumentMarshaler> booleanArgs = new HashMap<>();
   private Map<Character, ArgumentMarshaler> stringArgs = new HashMap<>();
   private Map<Character, ArgumentMarshaler> intArgs = new HashMap<>();
   private Set<Character> argsFound = new HashSet<>();

   private int currentArgument;
   private char errorArgumentId = '\0';
   private String errorParameter = "TILT";
   private ErrorCode errorCode = ErrorCode.OK;

   enum ErrorCode{
       OK, MISSING_STRING, UNEXPECTED_ARGUMENT, MISSING_INTEGER, INVALID_INTEGER;
   }

   public Args(String schema, String[] args) throws ParseException{
       this.schema = schema;
       this.args = args;
       valid = parse();
   }

   private boolean parse() throws ParseException {
       if(schema.length() == 0 && args.length == 0)return true;
       parseSchema();
       try {
           parseArguments();
       }catch (ArgsException ignored){}
       return valid;
   }
    private boolean parseSchema() throws ParseException {
        for(String element : schema.split(",")){
            if(element.length() > 0 ){
                String trimmedElement = element.trim();
                parseSchemaElement(trimmedElement);
            }
        }
        return true;
    }

    private void parseSchemaElement(String element) throws ParseException{
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if(isBooleanSchemaElement(elementTail))
            parseBooleanSchemaElement(elementId);
        else if (isStringSchemaElement(elementTail))
            parseStringSchemaElement(elementId);
        else if (isIntegerSchemaElement(elementTail))
            parseIntSchemaElement(elementId);
        else {
            throw new ParseException(
                    String.format("Argument: %c has invalid format: %s." , elementId, elementTail), 0);
        }
    }

    private void parseIntSchemaElement(char elementId) {
        intArgs.put(elementId, new IntegerArgumentMarshaler());
    }

    private boolean isIntegerSchemaElement(String elementTail) {
        return elementTail.equals("#");
    }

    private void parseBooleanSchemaElement(char elementId) {
        booleanArgs.put(elementId, new BooleanArgumentMarshaler());
    }

    private boolean isBooleanSchemaElement(String elementTail) {
        return elementTail.length() == 0;
    }

    private boolean isStringSchemaElement(String elementTail) {
        return elementTail.equals("*");
    }

    private void parseStringSchemaElement(char elementId) {
        stringArgs.put(elementId, new StringArgumentMarshaler());
    }

    private void validateSchemaElementId(char elementId) throws ParseException{
       if(! Character.isLetter(elementId)){
           throw  new ParseException("Bad character:" + elementId + "in Args format: " + schema,0);
       }
    }

    private boolean parseArguments()  throws ArgsException{
       for(currentArgument = 0; currentArgument<args.length ; currentArgument++){
           String arg = args[currentArgument];
           parseArgument(arg);
       }
       return true;
   }

   private void parseArgument(String arg)  throws ArgsException{
       if(arg.startsWith("-"))
           parseElements(arg);
   }

   private void parseElements(String arg)  throws ArgsException{
       for(int i = 1; i < arg.length() ; i++){
           parseElement(arg.charAt(i));
       }
   }

   private void parseElement(char argChar)  throws ArgsException{
       if(setArgument(argChar))
           argsFound.add(argChar);
       else {
           unexpectedArguments.add(argChar);
           errorCode = ErrorCode.UNEXPECTED_ARGUMENT;
           valid = false;
       }
   }

    private boolean setArgument(char argChar) throws ArgsException {
        if(isBoolean(argChar))
            setBooleanArg(argChar);
        else if(isString(argChar))
            setStringArg(argChar,"");
        else if(isIntArg(argChar))
            setIntArg(argChar);
        else return false;

        return true;
    }

    private void setIntArg(char argChar) throws ArgsException{
       currentArgument++;
       String parameter = null;
       try{
           parameter = args[currentArgument];
           intArgs.get(argChar).set(parameter);
       }catch (ArrayIndexOutOfBoundsException e){
           valid = false;
           errorArgumentId = argChar;
           errorCode = ErrorCode.MISSING_INTEGER;
           throw new ArgsException();
       }catch (Exception e){
           valid = false;
           errorArgumentId = argChar;
           errorParameter = parameter;
           errorCode = ErrorCode.INVALID_INTEGER;
           throw new ArgsException();
       }
    }

    private boolean isIntArg(char argChar) {
        return intArgs.containsKey(argChar);
    }

    private void setStringArg(char argChar, String s) throws ArgsException{
        currentArgument++;
        try{
            stringArgs.get(argChar).set(args[currentArgument]);
        }catch (ArrayIndexOutOfBoundsException e){
            valid=false;
            errorArgumentId = argChar;
            errorCode = ErrorCode.MISSING_STRING;
            throw new ArgsException();
        }
    }

    private boolean isString(char argChar) {
        return stringArgs.containsKey(argChar);
    }

    private void setBooleanArg(char argChar) {
        try {
            booleanArgs.get(argChar).set("true");
        } catch (ArgsException e) {

        }
    }

   private boolean isBoolean(char argChar) {
       return booleanArgs.containsKey(argChar);
   }

   public int cardinality(){
       return argsFound.size();
   }
   public String usage(){
       if(schema.length() > 0){
           return "-["+schema+"]";
       }else return "";
   }
   public String errorMessage() throws Exception{
       if(unexpectedArguments.size() > 0){
           return unexpectedArgumentMessage();
       }else{
           switch (errorCode){
               case UNEXPECTED_ARGUMENT ->
                       unexpectedArgumentMessage();
               case MISSING_STRING ->
                       String.format("Could not find string parameter for -%c.",errorArgumentId);
               case INVALID_INTEGER ->
                       String.format("Argument -%c expects an integer but was %s .",errorArgumentId, errorParameter);
               case MISSING_INTEGER ->
                       String.format("Could not find integer parameter for -%c.",errorArgumentId);
               case OK -> {throw new Exception("TILT: Should not get here.");}
           }
           return "";
       }
   }

   private String unexpectedArgumentMessage() {
       StringBuffer message = new StringBuffer("Argument(s) -");
       for(char c: unexpectedArguments){
           message.append(c);
       }
       message.append(" unexpected.");

       return message.toString();
   }
   public boolean getBoolean(char arg){
       ArgumentMarshaler am = booleanArgs.get(arg);
       return am!=null && (boolean) am.get();
   }

    public String getString(char arg){
        ArgumentMarshaler am = stringArgs.get(arg);
        return am==null? "" : (String) am.get();
    }

    public int getInt(char arg){
        ArgumentMarshaler am = intArgs.get(arg);
        return am==null? 0 : (int)am.get();
    }

    public boolean has(char arg){
       return argsFound.contains(arg);
    }

    public boolean isValid() {
        return valid;
    }

    private abstract class ArgumentMarshaler{
        public abstract void set(String s) throws ArgsException;
        public abstract Object get();
    }
    private class BooleanArgumentMarshaler extends ArgumentMarshaler{
        private boolean booleanValue = false;
        @Override
        public void set(String s) {
            booleanValue = true;
        }

        @Override
        public Object get() {
            return booleanValue;
        }
    }
    private class StringArgumentMarshaler extends ArgumentMarshaler{
        private String stringValue ="";
        @Override
        public void set(String s) {
            stringValue = s;
        }

        @Override
        public Object get() {
            return stringValue;
        }
    }
    private class IntegerArgumentMarshaler extends ArgumentMarshaler{
        private int intValue = 0;
        @Override
        public void set(String s) throws ArgsException{
            try{
            intValue = Integer.parseInt(s);

            }catch (NumberFormatException e){
                throw new ArgsException();
            }
        }
        @Override
        public Object get() {
            return intValue;
        }
    }
    private class ArgsException extends Exception{

    }
}
