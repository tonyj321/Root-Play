package play.annotations;

/**
 *
 * @author tonyj
 */
public class StreamerInfoException extends RuntimeException {

    StreamerInfoException(String message) {
        super(message);
    }

    StreamerInfoException(String message, StreamerInfoException x) {
        super(message,x);
    }
    
}
