package rides.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rides.dtos.RideDto;
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
	public Ride create(@RequestBody RideDto dto) {
		return ridesService.save(dto);
	}
	
	@GetMapping
	public List<Ride> findAll() {
		return ridesRepository.findAll();
	}

}
