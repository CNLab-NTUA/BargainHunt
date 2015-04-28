package gr.ntua.cn.zannis.bargains.client.exceptions;

/**
 * @author zannis <zannis.kal@gmail.com>
 */
public class UnexpectedInputException extends Throwable {

    private static final long serialVersionUID = 4669397038321228691L;

    public UnexpectedInputException() {
        super("Τα δεδομένα που εισάγατε είναι λάθος!!! ΜΠΟΥΜ");
    }
}
