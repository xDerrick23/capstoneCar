package dinodidiodoro.CarGo.bookings;

import dinodidiodoro.CarGo.exceptions.NotAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import dinodidiodoro.CarGo.exceptions.NotFoundException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingPayload bookingPayload) throws NotFoundException, NotAvailableException {
        return new ResponseEntity<>(bookingService.createBooking(bookingPayload), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public List<Booking> getAllBookings() {
        return bookingService.getBookings();
    }

    @GetMapping("/{id}")
    
    public Booking getBookingById(@PathVariable UUID id) throws NotFoundException {
        return bookingService.findById(id);
    }

    @GetMapping("/open")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Booking> getOpenBookings() {
        return bookingService.getOpenBookings();
    }

    @GetMapping("/closed")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Booking> getClosedBookings() {
        return bookingService.getClosedBookings();
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable UUID id, @RequestBody BookingPayload bookingPayload) throws NotFoundException, NotAvailableException {
        return new ResponseEntity<>(bookingService.updateBooking(id, bookingPayload), HttpStatus.OK);
    }
    
    @PutMapping("/admin/{id}")
    public ResponseEntity<Booking> updateBookingAdmin(@PathVariable UUID id, @RequestBody BookingPayload bookingPayload) throws NotFoundException, NotAvailableException {
        return new ResponseEntity<>(bookingService.updateBookingAdmin(id, bookingPayload), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable UUID id) throws NotFoundException {
        bookingService.findByIdAndDelete(id);
    }

    @DeleteMapping("/admin/{id}")
    public void deleteBookingAdmin(@PathVariable UUID id) throws NotFoundException {
        bookingService.deleteBookingAdmin(id);
    }
    
    @DeleteMapping("/deleteExpired")
    public void deleteExpiredBooking() {
    	bookingService.checkAndCancelOverlappingBookings();
    	bookingService.deleteExpiredBookings();
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Booking> closeBooking(@PathVariable UUID id) throws NotFoundException {
        return new ResponseEntity<>(bookingService.changeBookingState(id, Stato.CHIUSO), HttpStatus.OK);
    }
}

