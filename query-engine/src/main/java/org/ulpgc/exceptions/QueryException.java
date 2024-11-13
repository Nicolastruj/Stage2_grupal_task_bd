package org.ulpgc.exceptions;

<<<<<<< Updated upstream
public class QueryException extends Exception{
    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }
=======
public class QueryEngineException extends Exception{

    public QueryEngineException(String message, Throwable cause) {
        super(message, cause);}
>>>>>>> Stashed changes
}
