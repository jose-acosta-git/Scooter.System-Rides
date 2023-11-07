package rides.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rides.dtos.ScooterWithDistanceDto;
import rides.model.Ride;

public interface RidesRepository extends JpaRepository<Ride, Integer> {

    @Query("SELECT NEW rides.dtos.ScooterWithDistanceDto(r.scooterId, SUM(r.distance)) FROM Ride r GROUP BY r.scooterId ORDER BY SUM(r.distance) DESC")
    List<ScooterWithDistanceDto> findScootersOrderedByTotalDistance();
}