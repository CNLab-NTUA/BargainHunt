package gr.ntua.cn.zannis.bargains.client.exceptions;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class UnexpectedInputException extends Throwable {
    public UnexpectedInputException() {
        super("Τα δεδομένα που εισάγατε είναι λάθος!!! ΜΠΟΥΜ");
    }
}
