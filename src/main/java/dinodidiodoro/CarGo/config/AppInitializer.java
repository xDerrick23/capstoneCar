package dinodidiodoro.CarGo.config;

import dinodidiodoro.CarGo.bookings.Booking;
import dinodidiodoro.CarGo.bookings.BookingRepository;
import dinodidiodoro.CarGo.bookings.BookingService;
import dinodidiodoro.CarGo.bookings.Stato;
import dinodidiodoro.CarGo.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dinodidiodoro.CarGo.cars.CarPayload;
import dinodidiodoro.CarGo.cars.CarRepository;
import dinodidiodoro.CarGo.cars.CarService;
import dinodidiodoro.CarGo.exceptions.NotFoundException;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class AppInitializer {

	@Autowired
	CarRepository carRepository;

	@Autowired
    BookingRepository bookingRepository;
	
	@Autowired
    BookingService bookingService;
	
	@Autowired
    UsersService userService;

	@Autowired
	CarService carService;

	@PostConstruct
	public void init() {
		/*createCars();*/
		deleteExpiredBookings();
		/*changeRole();*/
		/*changeStato();*/
	}

	private void createCars() {
	    carService.createCar(new CarPayload("https://stellantis3.dam-broadcast.com/medias/domain12808/media105145/1543672-huhklv48yy-whr.jpg", "Jeep", "Avenger", "Giallo", "1.2 GSE T3", "1199cc", "100CV", "Benzina", "17km/l", 45.50));
	    carService.createCar(new CarPayload("https://cdn-img.automoto.it/images/27663098/3000x/00056768.jpeg", "Suzuki", "Swift", "Rosso", "Mild Hybrid (MHEV)", "1197cc", "83CV", "Benzina", "19km/l", 45.50));
	    carService.createCar(new CarPayload("https://immagini.alvolante.it/sites/default/files/styles/anteprima_lunghezza_640/public/news_galleria/2019/03/audi-a1-sportback-25-tfsi_2.jpg", "Audi", "A1", "Ciano", "1.0 TFSI", "999cc", "95CV", "Benzina", "19km/l", 45.50));
	    carService.createCar(new CarPayload("https://cdn.motor1.com/images/mgl/40Zw1M/s1/fiat-panda-city-life-2020.jpg", "Fiat", "Panda", "Bianco", "TwinAir", "875cc", "85CV", "Benzina", "14km/l", 30.50));
	    carService.createCar(new CarPayload("https://graphics.gestionaleauto.com/gonline_graphics/11664288_E_5d0a5a81bb6c4.jpg", "Ford", "Mustang", "Nero", "V8", "5038cc", "450CV", "Benzina", "9.6km/l", 90.00));
	    carService.createCar(new CarPayload("https://immagini.alvolante.it/sites/default/files/styles/anteprima_970/public/primo_contatto_galleria/2021/08/toyota-yaris-cross-2021-08.jpeg?itok=zeu76xYy", "Toyota", "Yaris Cross", "Bianco", "1.5 Hybrid 5p. E-CVT Active", "1490cc", "116CV", "Ibrida", "26km/l", 45.00));
	    carService.createCar(new CarPayload("https://www.biancoauto.it/wp-content/uploads/2023/06/IMG_8704-scaled.jpg", "Volkswagen", "Golf", "Nero", "2.0 TDI SCR", "1968cc", "115CV", "Diesel", "20km/l", 50.00));
	    carService.createCar(new CarPayload("https://cdn.motor1.com/images/mgl/40Zw1M/s1/fiat-panda-city-life-2020.jpg", "Fiat", "Panda", "Bianco", "TwinAir", "875cc", "85CV", "Benzina", "14km/l", 30.50));
	    carService.createCar(new CarPayload("https://cdn-img.automoto.it/images/15480684/3000x/00047612.jpeg", "Mercedes", "Classe A 200 Sport", "Grigio", "OM651", "1332cc", "163CV", "Benzina", "16km/l", 53.00));
	    carService.createCar(new CarPayload("https://cdn-img.automoto.it/images/21379908/3000x/bmw-x1-2019-14.jpeg", "BMW", "X1", "Grigio", "sDrive18d", "1995cc", "150CV", "Diesel", "20km/l", 45.50));
	}


	private void deleteExpiredBookings() {
		List<Booking> allBookings = bookingRepository.findAll();
		System.out.println(allBookings);
		for (Booking booking : allBookings) {
			if (booking.getDataFine().isBefore(LocalDate.now()) && booking.getStato().equals(Stato.APERTO)) {
				System.out.println(booking);
				Booking bookingDel = bookingRepository.findById(booking.getId())
						.orElseThrow(() -> new NotFoundException(booking.getId()));
				bookingRepository.delete(bookingDel);

			}
		}
	}
	
	private void changeRole() {
	UUID id = UUID.fromString("e0eb18d7-7fe6-4321-84d1-0671f5cdf474");
    userService.changeRole(id);
	}
	
	private void changeStato() {
		UUID id = UUID.fromString("48745550-003e-4660-9f5b-c8cd34ca022f");
	bookingService.changeBookingState(id, Stato.CHIUSO);
	}
}
