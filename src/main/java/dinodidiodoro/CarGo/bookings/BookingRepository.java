package dinodidiodoro.CarGo.bookings;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dinodidiodoro.CarGo.user.User;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID>{

	 List<Booking> findByUser(User user);
	 List<Booking> findByStato(Stato stato);

}