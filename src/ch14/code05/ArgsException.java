package ch14.code05;

class ArgsException extends Exception{
    private char errorArgumentId = '\0';
    private String errorParameter = "TILT";
    private ErrorCode errorCode = ErrorCode.OK;
    public ArgsException(){}
    public ArgsException(String message) {super(message);}
    public ArgsException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
    public ArgsException(ErrorCode errorCode, String errorParameter) {
        this.errorCode = errorCode;
        this.errorParameter = errorParameter;
    }

    public ArgsException(ErrorCode errorCode, char errorArgumentId, String errorParameter) {
        this.errorCode = errorCode;
        this.errorArgumentId = errorArgumentId;
        this.errorParameter = errorParameter;
    }

    public char getErrorArgumentId() {
        return errorArgumentId;
    }

    public void setErrorArgumentId(char errorArgumentId) {
        this.errorArgumentId = errorArgumentId;
    }

    public String getErrorParameter() {
        return errorParameter;
    }

    public void setErrorParameter(String errorParameter) {
        this.errorParameter = errorParameter;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String errorMessage() throws Exception {
            switch (errorCode) {
                case UNEXPECTED_ARGUMENT -> {
                    return String.format("Argument -%c unexpected.", errorArgumentId);
                }
                case MISSING_STRING -> {
                    return String.format("Could not find string parameter for -%c.", errorArgumentId);
                }
                case INVALID_INTEGER -> {
                    return String.format("Argument -%c expects an integer but was %s .", errorArgumentId, errorParameter);
                }
                case MISSING_INTEGER -> {
                    return String.format("Could not find integer parameter for -%c.", errorArgumentId);
                }
                case OK -> {
                    throw new Exception("TILT: Should not get here.");
                }
            }
            return "";
        }
    public enum ErrorCode {
        OK, MISSING_STRING, UNEXPECTED_ARGUMENT, MISSING_INTEGER, INVALID_INTEGER;
    }
}
