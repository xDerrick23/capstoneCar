package dinodidiodoro.CarGo.payment;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dinodidiodoro.CarGo.exceptions.NotFoundException;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
    
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Payment> createPayment() {
        Payment payment = paymentService.createPayment();
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Payment> getPaymentById(@PathVariable UUID paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID paymentId) {
        try {
            paymentService.deletePayment(paymentId);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}