package dinodidiodoro.CarGo.bookings;

import java.time.LocalDate;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BookingPayload {
	private LocalDate dataInizio;
	private LocalDate dataFine;
	private UUID carId;
}