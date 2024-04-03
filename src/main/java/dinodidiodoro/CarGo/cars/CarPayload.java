package dinodidiodoro.CarGo.cars;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class CarPayload {
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
}
