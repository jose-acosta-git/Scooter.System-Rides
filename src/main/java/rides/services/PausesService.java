package rides.services;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import rides.dtos.StartPauseDto;
import rides.model.Pause;
import rides.model.Ride;
import rides.repositories.PausesRepository;
import rides.repositories.RidesRepository;

@Service
public class PausesService {
	
	@Autowired
	private PausesRepository pausesRepository;
	@Autowired
	private RidesRepository ridesRepository;

	public ResponseEntity<Pause> startPause(StartPauseDto dto) {
		Optional<Ride> optionalRide = ridesRepository.findById(dto.getRideId());
		if (optionalRide.isPresent()) {
			Ride ride = optionalRide.get();
			if (ride.getEndTime() != null) {
				return ResponseEntity.badRequest().build();
			}
			Pause pause = new Pause(LocalTime.now(), ride);
			return ResponseEntity.ok(pausesRepository.save(pause));
		}
		return ResponseEntity.notFound().build();
	}

	public ResponseEntity<Pause> endPause(int pauseId) {
		Optional<Pause> optionalPause = pausesRepository.findById(pauseId);
		if (optionalPause.isPresent()) {
			Pause pause = optionalPause.get();
			pause.setEndTime(LocalTime.now());
			return ResponseEntity.ok(pausesRepository.save(pause));
		}
		return ResponseEntity.notFound().build();
	}
}