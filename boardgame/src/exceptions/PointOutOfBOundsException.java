package exceptions;

import java.awt.Point;

@SuppressWarnings("serial")
public class PointOutOfBOundsException extends IndexOutOfBoundsException {

	public PointOutOfBOundsException(Point newPoint) {
		super("The point '" + newPoint + "' is out of bounds.");
	}

	public PointOutOfBOundsException(String string) {
		super(string);
	}

}
