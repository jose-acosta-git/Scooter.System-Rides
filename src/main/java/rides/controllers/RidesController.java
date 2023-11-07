package rides.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rides.dtos.EndRideDto;
import rides.dtos.ScooterWithDistanceDto;
import rides.dtos.StartRideDto;
import rides.model.Ride;
import rides.repositories.RidesRepository;
import rides.services.RidesService;

@RestController
@RequestMapping("/rides")
public class RidesController {
	
	@Autowired
	RidesRepository ridesRepository;
	
	@Autowired
	RidesService ridesService;
	
	@PostMapping
	public ResponseEntity<Ride> startRide(@RequestBody StartRideDto dto) {
		return ridesService.startRide(dto);
	}
	
	@PatchMapping("/{rideId}/end")
	public ResponseEntity<Ride> endRide(@PathVariable int rideId, @RequestBody EndRideDto dto) {
		return ridesService.endRide(rideId, dto);
	}
	
	@GetMapping
	public List<Ride> findAll() {
		return ridesRepository.findAll();
	}
	
    @GetMapping("/scootersOrderedByDistance")
    public ResponseEntity<List<ScooterWithDistanceDto>> getScootersOrderedByDistance() {
        List<ScooterWithDistanceDto> scooterDistanceDTOs = ridesRepository.findScootersOrderedByTotalDistance();
        return ResponseEntity.ok(scooterDistanceDTOs);
    }

}
