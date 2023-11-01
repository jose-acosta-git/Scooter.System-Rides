package rides.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rides.dtos.RideDto;
import rides.model.Ride;
import rides.repositories.RidesRepository;

@Service
public class RidesService {
	
	@Autowired
	private RidesRepository ridesRepository;
	
	public Ride save(RideDto dto) {
		return ridesRepository.save(convertToEntity(dto));
	}
	
	private Ride convertToEntity(RideDto dto) {
		return new Ride(dto.getStartTime(), dto.getEndTime(), dto.getDistance(), dto.getPrice());
	}

}
