package dinodidiodoro.CarGo.user;

import dinodidiodoro.CarGo.bookings.Booking;
import dinodidiodoro.CarGo.bookings.Stato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import dinodidiodoro.CarGo.exceptions.NotFoundException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return usersService.getUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUserById(@PathVariable UUID id) {
        return usersService.findById(id);
    }
    
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public User getCurrentUser() {
        UUID currentUserId = usersService.getCurrentUserId();
        return usersService.findById(currentUserId);
    }

    @PutMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public User updateCurrentUser(@RequestBody NewUserPayload body) {
        UUID currentUserId = usersService.getCurrentUserId();
        return usersService.findByIdAndUpdate(currentUserId, body);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User updateUser(@PathVariable UUID id, @RequestBody NewUserPayload body) {
        return usersService.findByIdAndUpdate(id, body);
    }
    
    @PutMapping("/{id}/change-role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> changeRole(@PathVariable UUID id) {
        try {
            usersService.changeRole(id);
            User updatedUser = usersService.findById(id);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

  
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(@PathVariable UUID id) {
        usersService.findByIdAndDelete(id);
    }

    @DeleteMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public void deleteCurrentUser() {
        usersService.findByIdAndDelete(usersService.getCurrentUserId());
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUserByEmail(@PathVariable String email) {
        return usersService.findByEmail(email);
    }
    
    @GetMapping("/bookings/open")
    @PreAuthorize("isAuthenticated()")
    public List<Booking> getUserOpenBookings() {
        UUID currentUserId = usersService.getCurrentUserId();
        return usersService.findUserBookingsByState(currentUserId, Stato.APERTO);
    }

    @GetMapping("/bookings/closed")
    @PreAuthorize("isAuthenticated()")
    public List<Booking> getUserClosedBookings() {
        UUID currentUserId = usersService.getCurrentUserId();
        return usersService.findUserBookingsByState(currentUserId, Stato.CHIUSO);
    }
    
    @GetMapping("/current/bookings/open")
    public List<Booking> getOpenBookings() {
        return usersService.findCurrentUserBookingsByState(Stato.APERTO);
    }

    @GetMapping("/current/bookings/closed")
    public List<Booking> getClosedBookings() {
        return usersService.findCurrentUserBookingsByState(Stato.CHIUSO);
    }
}

