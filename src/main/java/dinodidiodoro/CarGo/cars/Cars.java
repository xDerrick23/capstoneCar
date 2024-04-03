package dinodidiodoro.CarGo.cars;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import dinodidiodoro.CarGo.bookings.Booking;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cars")
@NoArgsConstructor
@Data
public class Cars {

	@Id
	@GeneratedValue
	private UUID id;
	private String foto;
	private String marca;
	private String modello;
	private String colore;
	private String motore;
	private String cilindrata;
	private String potenza;
	private String tipoDiAlimentazione;
	private String consumoAKm;
	private Double costoGiornaliero;
	@OneToMany(mappedBy = "car")
	@JsonManagedReference
	private List<Booking> bookings = new ArrayList<>();

	public Cars(String foto, String marca, String modello, String colore, String motore, String cilindrata,
			String potenza, String tipoDiAlimentazione, String consumoAKm, Double costoGiornaliero) {
		this.foto = foto;
		this.marca = marca;
		this.modello = modello;
		this.colore = colore;
		this.motore = motore;
		this.cilindrata = cilindrata;
		this.potenza = potenza;
		this.tipoDiAlimentazione = tipoDiAlimentazione;
		this.consumoAKm = consumoAKm;
		this.costoGiornaliero = costoGiornaliero;
	}
}