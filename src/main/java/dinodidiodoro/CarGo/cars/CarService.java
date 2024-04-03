package dinodidiodoro.CarGo.cars;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import dinodidiodoro.CarGo.bookings.Booking;
import dinodidiodoro.CarGo.bookings.BookingRepository;
import dinodidiodoro.CarGo.bookings.Stato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import dinodidiodoro.CarGo.exceptions.NotFoundException;

@Service
public class CarService {

    @Autowired
    CarRepository carRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    public Cars save(Cars car) {
        return carRepository.save(car);
    }


    public Cars createCar(CarPayload body) {
        Cars newCar = new Cars(body.getFoto(), body.getMarca(), body.getModello(), body.getColore(),
                body.getMotore(), body.getCilindrata(), body.getPotenza(),
                body.getTipoDiAlimentazione(), body.getConsumoAKm(), body.getCostoGiornaliero());
        return carRepository.save(newCar);
    }

    public List<Cars> getAllCars() {
        return carRepository.findAll();
    }

    public Cars getCarById(UUID id) throws NotFoundException {
        return carRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public List<Cars> getCarsByMarca(String marca) {
        return carRepository.findByMarcaIgnoreCase(marca);
    }

    public List<Cars> getCarsByModello(String modello) {
        return carRepository.findByModelloIgnoreCase(modello);
    }

    public List<Cars> getCarsByColore(String colore) {
        return carRepository.findByColoreIgnoreCase(colore);
    }


    public List<Cars> getAllCarsSorted(String sortBy, String direction) {
        Sort sort = Sort.by(sortBy);
        if ("desc".equalsIgnoreCase(direction)) sort = sort.descending();
        else sort = sort.ascending();
        return carRepository.findAll(sort);
    }

    public Cars updateCar(UUID id, CarPayload body) throws NotFoundException {
        Cars carToUpdate = getCarById(id);
        carToUpdate.setFoto(body.getFoto());
        carToUpdate.setMarca(body.getMarca());
        carToUpdate.setModello(body.getModello());
        carToUpdate.setColore(body.getColore());
        carToUpdate.setMotore(body.getMotore());
        carToUpdate.setCilindrata(body.getCilindrata());
        carToUpdate.setPotenza(body.getPotenza());
        carToUpdate.setTipoDiAlimentazione(body.getTipoDiAlimentazione());
        carToUpdate.setConsumoAKm(body.getConsumoAKm());
        carToUpdate.setCostoGiornaliero(body.getCostoGiornaliero());
        List<Booking> associatedBookings = carToUpdate.getBookings();
        for (Booking booking : associatedBookings) {
            booking.setNomeModello(body.getModello());
            booking.setCostoTotale((ChronoUnit.DAYS.between(booking.getDataInizio(), booking.getDataFine())*body.getCostoGiornaliero()));
            bookingRepository.save(booking);
        }
        return carRepository.save(carToUpdate);
    }

    public void deleteCarById(UUID id) {
        Cars carToDelete = carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));

        List<Booking> bookings = carToDelete.getBookings();

        for(Booking booking: bookings) {
            if(booking.getStato() == Stato.CHIUSO) {
                booking.setCar(null);
                bookingRepository.save(booking);
            }
        }
        bookingRepository.deleteAll(
            carToDelete.getBookings().stream()
                .filter(booking -> booking.getStato() == Stato.APERTO)
                .collect(Collectors.toList())
        );
        carRepository.delete(carToDelete);
    }

}
