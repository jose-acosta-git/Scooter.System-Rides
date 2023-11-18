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

import jakarta.servlet.http.HttpServletRequest;
import rides.dtos.StartPauseDto;
import rides.model.Pause;
import rides.repositories.PausesRepository;
import rides.services.PausesService;

@RestController
@RequestMapping("/pauses")
public class PausesController {
	
	@Autowired
	private PausesService pausesService;
	
	@PostMapping
	public ResponseEntity<Pause> startPause(HttpServletRequest request, @RequestBody StartPauseDto dto) {
		return pausesService.startPause(request, dto);
	}

	@PatchMapping("{pauseId}/end")
	public ResponseEntity<Pause> endPause(HttpServletRequest request, @PathVariable int pauseId) {
		return pausesService.endPause(request, pauseId);
	}
	
	@GetMapping
	public ResponseEntity<List<Pause>> findAll(HttpServletRequest request) {
		return pausesService.findAll(request);
	}
}
