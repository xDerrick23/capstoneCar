package dinodidiodoro.CarGo.cars;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Cars, UUID>{

	@Query("SELECT c FROM Cars c WHERE LOWER(c.marca) = LOWER(:marca)")
	List<Cars> findByMarcaIgnoreCase(@Param("marca") String marca);

	@Query("SELECT c FROM Cars c WHERE LOWER(c.modello) = LOWER(:modello)")
	List<Cars> findByModelloIgnoreCase(@Param("modello") String modello);

	@Query("SELECT c FROM Cars c WHERE LOWER(c.colore) = LOWER(:colore)")
	List<Cars> findByColoreIgnoreCase(@Param("colore") String colore);

    
}