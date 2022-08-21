package ch14.code05;

import java.text.ParseException;
import java.util.*;

import static ch14.code05.ArgsException.ErrorCode.*;


class Args {
    private String schema;
    private List<String> argsList;
    private boolean valid = true;
    private Set<Character> unexpectedArguments = new TreeSet<>();
    private Map<Character, ArgumentMarshaler> marshalers = new HashMap<>();
    private Set<Character> argsFound = new HashSet<>();
    private Iterator<String> currentArgument;

    public Args(String schema, String[] args) throws ArgsException {
        this.schema = schema;
        this.argsList = Arrays.asList(args);
        valid = parse();
    }

    private boolean parse() throws ArgsException {
        if (schema.length() == 0 && argsList.size() == 0) return true;
        parseSchema();
        try {
            parseArguments();
        } catch (ArgsException ignored) {
        }
        return valid;
    }

    private boolean parseSchema() throws ArgsException {
        for (String element : schema.split(",")) {
            if (element.length() > 0) {
                String trimmedElement = element.trim();
                parseSchemaElement(trimmedElement);
            }
        }
        return true;
    }

    private void parseSchemaElement(String element) throws ArgsException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (isBooleanSchemaElement(elementTail))
            marshalers.put(elementId, new BooleanArgumentMarshaler());
        else if (isStringSchemaElement(elementTail))
            marshalers.put(elementId, new StringArgumentMarshaler());
        else if (isIntegerSchemaElement(elementTail))
            marshalers.put(elementId, new IntegerArgumentMarshaler());
        else {
            throw new ArgsException(
                    String.format("Argument: %c has invalid format: %s.", elementId, elementTail));
        }
    }

    private boolean isBooleanSchemaElement(String elementTail) {
        return elementTail.length() == 0;
    }

    private boolean isStringSchemaElement(String elementTail) {
        return elementTail.equals("*");
    }

    private boolean isIntegerSchemaElement(String elementTail) {
        return elementTail.equals("#");
    }

    private void validateSchemaElementId(char elementId) throws ArgsException {
        if (!Character.isLetter(elementId)) {
            throw new ArgsException("Bad character:" + elementId + "in Args format: " + schema);
        }
    }

    private boolean parseArguments() throws ArgsException {
        for (currentArgument = argsList.iterator(); currentArgument.hasNext(); ) {
            String arg = currentArgument.next();
            parseArgument(arg);
        }
        return true;
    }

    private void parseArgument(String arg) throws ArgsException {
        if (arg.startsWith("-"))
            parseElements(arg);
    }

    private void parseElements(String arg) throws ArgsException {
        for (int i = 1; i < arg.length(); i++) {
            parseElement(arg.charAt(i));
        }
    }

    private void parseElement(char argChar) throws ArgsException {
        if (setArgument(argChar))
            argsFound.add(argChar);
        else {
            unexpectedArguments.add(argChar);
            valid = false;
            throw new ArgsException(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT);
        }
    }

    private boolean setArgument(char argChar) throws ArgsException {
        ArgumentMarshaler m = marshalers.get(argChar);
        if (m == null) return false;
        try {
            m.set(currentArgument);
        } catch (ArgsException e) {
            valid = false;
            e.setErrorArgumentId(argChar);
            throw e;
        }
        return true;
    }

    public int cardinality() {
        return argsFound.size();
    }

    public String usage() {
        if (schema.length() > 0) {
            return "-[" + schema + "]";
        } else return "";
    }

    public boolean getBoolean(char arg) {
        ArgumentMarshaler am = marshalers.get(arg);
        return am != null && (boolean) am.get();
    }

    public String getString(char arg) {
        ArgumentMarshaler am = marshalers.get(arg);
        return am == null ? "" : (String) am.get();
    }

    public int getInt(char arg) {
        ArgumentMarshaler am = marshalers.get(arg);
        return am == null ? 0 : (int) am.get();
    }

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

    public boolean isValid() {
        return valid;
    }

    private interface ArgumentMarshaler {
        void set(Iterator<String> currentArgument) throws ArgsException;
        Object get();
    }

    private class BooleanArgumentMarshaler implements ArgumentMarshaler {
        private boolean booleanValue = false;
        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            booleanValue = true;
        }

        @Override
        public Object get() {
            return booleanValue;
        }
    }

    private class StringArgumentMarshaler implements ArgumentMarshaler {
        private String stringValue = "";
        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            try{
                stringValue = currentArgument.next();
            }catch (NoSuchElementException e){
                throw new ArgsException(MISSING_STRING);
            }

        }
        @Override
        public Object get() {
            return stringValue;
        }
    }

    private class IntegerArgumentMarshaler implements ArgumentMarshaler {
        private int intValue = 0;
        @Override
        public void set(Iterator<String> currentArgument) throws ArgsException {
            String parameter = null;
            try {
                parameter = currentArgument.next();
                intValue = Integer.parseInt(parameter);
            } catch (NoSuchElementException e) {
                throw new ArgsException(MISSING_INTEGER);
            } catch (NumberFormatException e) {
                throw new ArgsException(INVALID_INTEGER);
            }
        }

        @Override
        public Object get() {
            return intValue;
        }
    }
}
