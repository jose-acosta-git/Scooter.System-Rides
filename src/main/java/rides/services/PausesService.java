package rides.services;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import rides.dtos.StartPauseDto;
import rides.model.Pause;
import rides.model.Ride;
import rides.model.User;
import rides.repositories.PausesRepository;
import rides.repositories.RidesRepository;

@Service
public class PausesService {
	
	@Autowired
	private PausesRepository pausesRepository;
	@Autowired
	private RidesRepository ridesRepository;
	@Autowired
	private AuthService authService;

	public ResponseEntity<Pause> startPause(HttpServletRequest request, StartPauseDto dto) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		User user = authService.getUserFromToken(token);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Optional<Ride> optionalRide = ridesRepository.findById(dto.getRideId());
		if (optionalRide.isPresent()) {
			Ride ride = optionalRide.get();
			if (ride.getUserId() != user.getId()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			if (ride.getEndTime() != null) {
				return ResponseEntity.badRequest().build();
			}
			Pause pause = new Pause(LocalTime.now(), ride);
			return ResponseEntity.ok(pausesRepository.save(pause));
		}
		return ResponseEntity.notFound().build();
	}

	public ResponseEntity<Pause> endPause(HttpServletRequest request, int pauseId) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		User user = authService.getUserFromToken(token);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}


		Optional<Pause> optionalPause = pausesRepository.findById(pauseId);
		if (optionalPause.isPresent()) {
			Pause pause = optionalPause.get();
			if (pause.getRide().getUserId() != user.getId()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			pause.setEndTime(LocalTime.now());
			return ResponseEntity.ok(pausesRepository.save(pause));
		}
		return ResponseEntity.notFound().build();
	}

	public ResponseEntity<List<Pause>> findAll(HttpServletRequest request) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String role = authService.getRoleFromToken(token);
		if (role != null && (role.equals("ADMIN") || role.equals("MAINTENANCE"))) {
			return ResponseEntity.ok(pausesRepository.findAll());
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}
}