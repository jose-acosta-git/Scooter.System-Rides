package rides.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import rides.model.Ride;

public interface RidesRepository extends JpaRepository<Ride, Integer> {

}
