package dinodidiodoro.CarGo.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dinodidiodoro.CarGo.bookings.Booking;
import dinodidiodoro.CarGo.bookings.BookingRepository;
import dinodidiodoro.CarGo.bookings.Stato;
import dinodidiodoro.CarGo.exceptions.NotFoundException;
import dinodidiodoro.CarGo.user.User;
import dinodidiodoro.CarGo.user.UsersService;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;
    
    @Autowired
    UsersService userService;
    
    @Autowired
    BookingRepository bookingRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment createPayment() {
        User user = userService.findById(userService.getCurrentUserId());
        
        List<Booking> allUserBookings = bookingRepository.findByUser(user);
        
        List<Booking> openBookings = allUserBookings.stream()
                .filter(booking -> booking.getStato() == Stato.APERTO)
                .collect(Collectors.toList());
        
        if(openBookings.isEmpty()){
            throw new IllegalStateException("Non ci sono Booking con stato APERTO");
        }
        
        double totalCost = 0;
        for(Booking booking : openBookings){
            totalCost += booking.getCostoTotale();
            booking.setStato(Stato.CHIUSO);
            bookingRepository.save(booking);
        }
        
        Payment payment = new Payment(totalCost, user.getEmail(), new ArrayList<>(), user);
        payment = paymentRepository.save(payment);
        
        for(Booking booking : openBookings){
            booking.setPayment(payment);
            bookingRepository.save(booking);
            payment.getBookings().add(booking);
        }
        
        return paymentRepository.save(payment);
    }

    
    public void deletePayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                              .orElseThrow(() -> new NotFoundException("Payment non trovato"));
        
        List<Booking> associatedBookings = payment.getBookings();
        for(Booking booking : associatedBookings){
            booking.setPayment(null);
            bookingRepository.save(booking);
        }
        User user = payment.getUser();
        if(user != null) {
            user.getPayments().remove(payment);
            userService.save(user);
        }
        
        paymentRepository.delete(payment);
    }

    
    public Payment getPaymentById(UUID paymentId) throws NotFoundException {
        return paymentRepository.findById(paymentId)
                                .orElseThrow(() -> new NotFoundException("Pagamento non trovato con id: " + paymentId));
    }
    
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }
}