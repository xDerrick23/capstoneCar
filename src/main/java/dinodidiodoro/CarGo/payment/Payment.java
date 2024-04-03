package dinodidiodoro.CarGo.payment;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import dinodidiodoro.CarGo.bookings.Booking;
import dinodidiodoro.CarGo.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@Data
public class Payment {
	@Id
    @GeneratedValue
    private UUID id;
	private LocalDate dataPagamento = LocalDate.now();
	private Double costo;
	private String emailAcquirente;
	@OneToMany(mappedBy = "payment")
	@JsonManagedReference
	private List<Booking> bookings;
	@ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
     private User user;
	public Payment(Double costo, String emailAcquirente, List<Booking> bookings, User user) {
		this.costo = costo;
		this.emailAcquirente = emailAcquirente;
		this.bookings = bookings;
		this.user = user;
	}
	
}