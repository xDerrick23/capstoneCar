package dinodidiodoro.CarGo.cars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import dinodidiodoro.CarGo.exceptions.NotFoundException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cars")
public class CarsController {

    @Autowired
    private CarService carService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Cars createCar(@RequestBody CarPayload carPayload) {
        return carService.createCar(carPayload);
    }

    @GetMapping
    public List<Cars> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    public Cars getCarById(@PathVariable UUID id) throws NotFoundException {
        return carService.getCarById(id);
    }

    @GetMapping("/search/marca")
    public List<Cars> getCarsByMarca(@RequestParam String marca) {
        return carService.getCarsByMarca(marca);
    }

    @GetMapping("/search/modello")
    public List<Cars> getCarsByModello(@RequestParam String modello) {
        return carService.getCarsByModello(modello);
    }

    @GetMapping("/search/colore")
    public List<Cars> getCarsByColore(@RequestParam String colore) {
        return carService.getCarsByColore(colore);
    }

    @GetMapping("/sorted")
    public List<Cars> getAllCarsSorted(
            @RequestParam(name = "sortBy", defaultValue = "marca") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction) {
        return carService.getAllCarsSorted(sortBy, direction);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Cars updateCar(@PathVariable UUID id, @RequestBody CarPayload carPayload) throws NotFoundException {
        return carService.updateCar(id, carPayload);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteCar(@PathVariable UUID id) throws NotFoundException {
        carService.deleteCarById(id);
    }
}

