package play;

/**
 *
 * @author tonyj
 */
class StreamerInfoException extends RuntimeException {

    StreamerInfoException(String message) {
        super(message);
    }

    StreamerInfoException(String message, StreamerInfoException x) {
        super(message,x);
    }
    
}
