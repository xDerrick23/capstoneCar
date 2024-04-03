package dinodidiodoro.CarGo.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import dinodidiodoro.CarGo.bookings.Booking;
import dinodidiodoro.CarGo.bookings.BookingRepository;
import dinodidiodoro.CarGo.bookings.Stato;
import dinodidiodoro.CarGo.exceptions.BadRequestException;
import dinodidiodoro.CarGo.payment.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import dinodidiodoro.CarGo.exceptions.NotFoundException;
import dinodidiodoro.CarGo.payment.Payment;


@Service
public class UsersService {

	@Autowired
	BookingRepository bookingRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PaymentRepository paymentRepository;

	public User save(User user) {
	    return userRepository.save(user);
	}

	public User save(NewUserPayload body) {
		userRepository.findByEmail(body.getEmail()).ifPresent(user -> {
			throw new BadRequestException("L'email " + body.getEmail() + " è gia stata utilizzata");
		});
		User newUser = new User(body.getName(), body.getSurname(), body.getEmail(), body.getPassword());
		return userRepository.save(newUser);
	}

	public List<User> getUsers() {
		return userRepository.findAll();
	}

	public User findById(UUID id) throws NotFoundException {
		return userRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
	}

	public User findByIdAndUpdate(UUID id, NewUserPayload body) throws NotFoundException {
		User found = this.findById(id);
		found.setName(body.getName());
		found.setSurname(body.getSurname());
		found.setEmail(body.getEmail());
		List<Booking> associatedBookings = found.getBookings();
	    for (Booking booking : associatedBookings) {
	        booking.setEmailAcquirente(body.getEmail());
	        bookingRepository.save(booking);
	    }
		return userRepository.save(found);
	}

	public void findByIdAndDelete(UUID id) {
	    User user = userRepository.findById(id)
	            .orElseThrow(() -> new NotFoundException(id));
	    
	    List<Payment> payments = user.getPayments();
	    if(payments != null) {
	        List<Payment> modifiedPayments = new ArrayList<>();
	        for(Payment payment : payments){
	            payment.setUser(null);
	            modifiedPayments.add(payment);
	        }
	        paymentRepository.saveAll(modifiedPayments);
	    }
	    
	    List<Booking> bookings = user.getBookings();
	    if(bookings != null) {
	        List<Booking> modifiedBookings = new ArrayList<>();
	        for(Booking booking: bookings) {
	            if(booking.getStato() == Stato.CHIUSO) {
	                booking.setUser(null);
	                modifiedBookings.add(booking);
	            }
	        }
	        bookingRepository.saveAll(modifiedBookings);

	        List<Booking> bookingsToDelete = bookings.stream()
	            .filter(booking -> booking.getStato() == Stato.APERTO)
	            .collect(Collectors.toList());
	        bookingRepository.deleteAll(bookingsToDelete);
	    }
	    userRepository.delete(user);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato"));
	}

	public void changeRole(UUID id) throws NotFoundException {
		User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
		if (user.getRole() == Role.USER) {
			user.setRole(Role.ADMIN);
		} else {
			user.setRole(Role.USER);
		}
		userRepository.save(user);
	}

	public List<Booking> findUserBookingsByState(UUID userId, Stato state) {
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
		return user.getBookings().stream().filter(booking -> booking.getStato() == state).collect(Collectors.toList());
	}

	public List<Booking> findCurrentUserBookingsByState(Stato state) {
	    UUID userId = getCurrentUserId();
	    User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
	    return user.getBookings().stream()
	               .filter(booking -> booking.getStato() == state)
	               .collect(Collectors.toList());
	}

	
	public UUID getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();
		return currentUser.getId();
	}
	
	

}