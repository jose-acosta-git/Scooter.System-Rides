package rides.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import rides.model.Pause;

public interface PausesRepository extends JpaRepository<Pause, Integer> {}