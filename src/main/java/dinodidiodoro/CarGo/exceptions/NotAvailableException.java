package dinodidiodoro.CarGo.exceptions;

import java.util.UUID;
@SuppressWarnings("serial")
public class NotAvailableException extends RuntimeException {
	public NotAvailableException(String message) {
		super(message);
	}

	public NotAvailableException(UUID  id) {
		super(id + " non disponibile!");
	}
}
