package dinodidiodoro.CarGo.bookings;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import dinodidiodoro.CarGo.exceptions.NotAvailableException;
import dinodidiodoro.CarGo.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dinodidiodoro.CarGo.cars.CarService;
import dinodidiodoro.CarGo.cars.Cars;
import dinodidiodoro.CarGo.exceptions.NotFoundException;
import dinodidiodoro.CarGo.payment.Payment;
import dinodidiodoro.CarGo.user.User;
import dinodidiodoro.CarGo.user.UsersService;

@Service
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    CarService carService;
    
    @Autowired
    UsersService userService;
    
    @Autowired
    PaymentService paymentService;

    public Booking createBooking(BookingPayload bookingPayload) throws NotFoundException, NotAvailableException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) throw new IllegalArgumentException("Authentication not found.");

        User currentUser = (User) authentication.getPrincipal();
        if (currentUser == null) throw new IllegalArgumentException("Current user not found.");

        UUID carId = bookingPayload.getCarId();
        Cars car = carService.getCarById(carId);

        if (car == null) throw new NotFoundException("Auto non trovata.");

        LocalDate dataInizio = bookingPayload.getDataInizio();
        LocalDate dataFine = bookingPayload.getDataFine();

        if (dataInizio == null || dataFine == null) throw new IllegalArgumentException("Le date non possono essere nulle");
        if (dataInizio.isBefore(LocalDate.now()) || dataFine.isBefore(LocalDate.now())) throw new IllegalArgumentException("Le date non possono essere precedenti alla data corrente");
        if (dataInizio.isAfter(dataFine)) throw new IllegalArgumentException("Le date non sono state inserite nell'ordine corretto");
        
        List<Booking> overlappingBookingsUser = currentUser.getBookings().stream()
            .filter(booking -> booking != null && booking.getStato() == Stato.APERTO)
            .filter(booking -> booking.getCar() != null && booking.getCar().getId().equals(carId))
            .filter(booking -> isDateOverlap(booking.getDataInizio(), booking.getDataFine(), dataInizio, dataFine))
            .collect(Collectors.toList());

        if (!overlappingBookingsUser.isEmpty()) throw new NotAvailableException("Date inserite non disponibili per questa auto.");

        List<Booking> overlappingBookingsCar = car.getBookings().stream()
            .filter(booking -> booking != null && booking.getStato() == Stato.CHIUSO)
            .filter(booking -> isDateOverlap(booking.getDataInizio(), booking.getDataFine(), dataInizio, dataFine))
            .collect(Collectors.toList());

        if (!overlappingBookingsCar.isEmpty()) throw new NotAvailableException("Date prenotate non disponibili per questa auto.");

        long daysBetween = ChronoUnit.DAYS.between(dataInizio, dataFine);
        Booking newBooking = new Booking(dataInizio, dataFine);
        newBooking.setNomeModello(car.getModello());
        newBooking.setEmailAcquirente(currentUser.getEmail());
        newBooking.setCostoTotale(daysBetween * car.getCostoGiornaliero());
        newBooking.setCar(car);
        newBooking.setUser(currentUser);
        newBooking.setStato(Stato.APERTO);
        currentUser.getBookings().add(newBooking);
        car.getBookings().add(newBooking);

        return save(newBooking);
    }



    public List<Booking> getBookings() {
    	checkAndCancelOverlappingBookings();
    	deleteExpiredBookings();
        return bookingRepository.findAll();
    }

    public Booking findById(UUID id) throws NotFoundException {
        return bookingRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public Booking updateBooking(UUID id, BookingPayload bookingPayload) throws NotFoundException, NotAvailableException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Booking existingBooking = findById(id);
        Cars car = carService.getCarById(existingBooking.getCar().getId());

        if (car == null) throw new NotFoundException("Auto non trovata.");

        LocalDate dataInizio = bookingPayload.getDataInizio();
        LocalDate dataFine = bookingPayload.getDataFine();

        if (dataInizio == null || dataFine == null) throw new IllegalArgumentException("Le date non possono essere nulle");
        if (dataInizio.isBefore(LocalDate.now()) || dataFine.isBefore(LocalDate.now())) throw new IllegalArgumentException("Le date non possono essere precedenti alla data corrente");
        
        UUID carId = existingBooking.getCar().getId();
        List<Booking> overlappingBookingsUser = currentUser.getBookings().stream()
        		.filter(booking -> !booking.getId().equals(id))
                .filter(booking -> booking.getCar().getId().equals(carId))
                .filter(booking -> booking.getStato() == Stato.APERTO)
                .filter(booking -> isDateOverlap(booking.getDataInizio(), booking.getDataFine(), dataInizio, dataFine))
                .collect(Collectors.toList());

        if (!overlappingBookingsUser.isEmpty()) throw new NotAvailableException("Date inserite non disponibili per questa auto.");

        List<Booking> overlappingBookings = car.getBookings().stream()
        		.filter(booking -> !booking.getId().equals(id))
                .filter(booking -> booking.getStato() == Stato.CHIUSO)
                .filter(booking -> isDateOverlap(booking.getDataInizio(), booking.getDataFine(), dataInizio, dataFine))
                .collect(Collectors.toList());

        if (!overlappingBookings.isEmpty()) throw new NotAvailableException("Date prenotate non disponibili per questa auto.");

        long daysBetween = ChronoUnit.DAYS.between(dataInizio, dataFine);
        existingBooking.setDataInizio(dataInizio);
        existingBooking.setDataFine(dataFine);
        existingBooking.setCostoTotale(daysBetween * car.getCostoGiornaliero());
        return save(existingBooking);
    }
    
    public Booking updateBookingAdmin(UUID id, BookingPayload bookingPayload) throws NotFoundException, NotAvailableException {
 
        Booking existingBooking = findById(id);
        Cars car = carService.getCarById(existingBooking.getCar().getId());

        if (car == null) throw new NotFoundException("Auto non trovata.");

        LocalDate dataInizio = bookingPayload.getDataInizio();
        LocalDate dataFine = bookingPayload.getDataFine();

        if (dataInizio == null || dataFine == null) throw new IllegalArgumentException("Le date non possono essere nulle");
        if (dataInizio.isBefore(LocalDate.now()) || dataFine.isBefore(LocalDate.now())) throw new IllegalArgumentException("Le date non possono essere precedenti alla data corrente");
        
        List<Booking> overlappingBookings = car.getBookings().stream()
        		.filter(booking -> !booking.getId().equals(id))
                .filter(booking -> booking.getStato() == Stato.CHIUSO)
                .filter(booking -> isDateOverlap(booking.getDataInizio(), booking.getDataFine(), dataInizio, dataFine))
                .collect(Collectors.toList());

        if (!overlappingBookings.isEmpty()) throw new NotAvailableException("Date prenotate non disponibili per questa auto.");

        long daysBetween = ChronoUnit.DAYS.between(dataInizio, dataFine);
        existingBooking.setDataInizio(dataInizio);
        existingBooking.setDataFine(dataFine);
        existingBooking.setCostoTotale(daysBetween * car.getCostoGiornaliero());
        return save(existingBooking);
    }

    public void deleteBookingAdmin(UUID id) throws NotFoundException {
        Booking booking = findById(id);
        if (booking.getCar() != null) {
            Cars car = carService.getCarById(booking.getCar().getId());
            if (car != null) {
                car.getBookings().remove(booking);
                carService.save(car);
            }
        }
        if (booking.getPayment() != null) {
            Payment payment = paymentService.getPaymentById(booking.getPayment().getId());
            if (payment != null) {
                payment.getBookings().remove(booking);
                paymentService.save(payment);
            }
        }
        bookingRepository.delete(booking);
    }


    public void findByIdAndDelete(UUID id) {
        Booking booking = findById(id);
        User user = userService.findById(booking.getUser().getId());
        Cars car = carService.getCarById(booking.getCar().getId());
        Payment payment = null;
        if (booking.getPayment() != null) {
            payment = paymentService.getPaymentById(booking.getPayment().getId());
            payment.getBookings().remove(booking);
            paymentService.save(payment);
        }
        user.getBookings().remove(booking);
        car.getBookings().remove(booking);
  
        userService.save(user);
        carService.save(car);

        bookingRepository.delete(booking);
    }

    public Booking changeBookingState(UUID id, Stato newState) throws NotFoundException {
        Booking found = findById(id);
        found.setStato(newState);
        return save(found);
    }
    
    public List<Booking> getOpenBookings() {
    	checkAndCancelOverlappingBookings();
    	deleteExpiredBookings();
        return bookingRepository.findByStato(Stato.APERTO);
        
    }

    public List<Booking> getClosedBookings() {
    	checkAndCancelOverlappingBookings();
    	deleteExpiredBookings();
        return bookingRepository.findByStato(Stato.CHIUSO);
    }


    public static boolean isDateOverlap(LocalDate startDate1, LocalDate endDate1, LocalDate startDate2, LocalDate endDate2) {
        if (startDate1 == null || endDate1 == null || startDate2 == null || endDate2 == null)
            throw new IllegalArgumentException("Le date non possono essere nulle");
        
        if (startDate1.isAfter(endDate1))
            throw new IllegalArgumentException("startDate1 non può essere dopo endDate1");
        
        if (startDate2.isAfter(endDate2))
            throw new IllegalArgumentException("startDate2 non può essere dopo endDate2");
        
        if (startDate1.isEqual(endDate1))
            throw new IllegalArgumentException("La data di inizio e la data di fine startDate1 e endDate1 non possono essere uguali");
        
        if (startDate2.isEqual(endDate2))
            throw new IllegalArgumentException("La data di inizio e la data di fine startDate2 e endDate2 non possono essere uguali");
        
        return (startDate1.isEqual(startDate2) || startDate1.isAfter(startDate2)) && startDate1.isBefore(endDate2) ||
               (endDate1.isEqual(endDate2) || endDate1.isBefore(endDate2)) && endDate1.isAfter(startDate2) ||
               (startDate2.isEqual(startDate1) || startDate2.isAfter(startDate1)) && startDate2.isBefore(endDate1) ||
               (endDate2.isEqual(endDate1) || endDate2.isBefore(endDate1)) && endDate2.isAfter(startDate1);
    }

    public void checkAndCancelOverlappingBookings() {
        List<Booking> openBookings = bookingRepository.findByStato(Stato.APERTO);
        List<Booking> closedBookings = bookingRepository.findByStato(Stato.CHIUSO);

        for (Booking open : openBookings) {
            if (open.getCar() == null) {
                continue;
            }
            for (Booking closed : closedBookings) {
                if (closed.getCar() == null) {
                    continue;
                }
                if (open.getCar().getId().equals(closed.getCar().getId())
                    && isDateOverlap(open.getDataInizio(), open.getDataFine(), closed.getDataInizio(), closed.getDataFine())) {
                    
                    open.setUser(null);
                    open.setCar(null);
                    bookingRepository.save(open);
                    bookingRepository.delete(open);
                }
            }
        }
    }


    public void deleteExpiredBookings() {
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
    
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }
}
