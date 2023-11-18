package rides.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import rides.dtos.EndRideDto;
import rides.dtos.PaymentDto;
import rides.dtos.ScooterWithTimeDto;
import rides.dtos.StartRideDto;
import rides.model.Pause;
import rides.model.Ride;
import rides.model.User;
import rides.repositories.RidesRepository;

@Service
public class RidesService {
	
	@Autowired
	private RidesRepository ridesRepository;
	@Autowired
	private AuthService authService;
	
	private HttpClient client = HttpClient.newHttpClient();

	public ResponseEntity<Ride> startRide(HttpServletRequest request, StartRideDto dto) {
		String token = authService.getTokenFromRequest(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		User user = authService.getUserFromToken(token);
		if (user == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		if (!authService.canStartRide(token)) {
			return ResponseEntity.badRequest().build();
		}

		String url = "http://localhost:8888/scooters/" + dto.getScooterId() + "/currentStop";
        HttpRequest scooterRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + token)
            .build();

		try {
			HttpResponse<String> response = client.send(scooterRequest, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200 && response.body() != null && !response.body().isEmpty()) {
				return ResponseEntity.ok(ridesRepository.save(convertToEntity(dto, user.getId())));
			}
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
	/* 
	public ResponseEntity<Ride> endRide(int rideId, EndRideDto dto) {
		//Verifica que el viaje exista
		Optional<Ride> optionalRide = ridesRepository.findById(rideId);
		if (!optionalRide.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		//Verifica que el viaje no haya finalizado
		Ride ride = optionalRide.get();
		if (ride.getEndTime() != null) {
			return ResponseEntity.badRequest().build();
		}
		
		boolean scooterIsInAStop = checkScooterInStop(ride.getScooterId());
		if (!scooterIsInAStop) {
			return ResponseEntity.badRequest().build();
		}
		
		//Obtiene las tarifas actuales
		String standardPriceResponse = getOk("http://localhost:9090/fares/currentStandardPrice");
		String extendedPausePriceResponse = getOk("http://localhost:9090/fares/currentExtendedPausePrice");
		if (standardPriceResponse == null || extendedPausePriceResponse == null) {
			return ResponseEntity.badRequest().build();
		}

		//Inicializa variables
		LocalDateTime endTime = LocalDateTime.now();
		
		//Calcula el precio del viaje
		double totalPrice = 0;
		double standardPrice = Double.valueOf(standardPriceResponse);
		double extendedPausePrice = Double.valueOf(extendedPausePriceResponse);
		LocalTime higherRateStartTime = null;
		
		for (Pause pause : ride.getPauses()) {
			Duration pauseDuration = Duration.between(pause.getStartTime(), pause.getEndTime());
			long pauseSeconds = pauseDuration.getSeconds();
			
			if (pauseSeconds > 900) {
				LocalTime currentHigherRateStartTime = pause.getStartTime().plusMinutes(15);
				if (higherRateStartTime == null) {
					higherRateStartTime = currentHigherRateStartTime;
				} else if (currentHigherRateStartTime.isBefore(higherRateStartTime)) {
					higherRateStartTime = currentHigherRateStartTime;
				}
			}
		}
		
		if (higherRateStartTime == null) {
			Duration totalDuration = Duration.between(ride.getStartTime(), endTime);
			totalPrice += totalDuration.getSeconds() * standardPrice;
		} else {
			Duration standardRateTime = Duration.between(ride.getStartTime().toLocalTime(), higherRateStartTime);
			totalPrice += standardRateTime.getSeconds() * standardPrice;
			Duration higherRateTime = Duration.between(higherRateStartTime, endTime);
			totalPrice += higherRateTime.getSeconds() * extendedPausePrice;
		}

		
		//Establece los valores del viaje
		ride.setEndTime(endTime);
		ride.setDistance(dto.getDistance());
		ride.setPrice(totalPrice);
		
		//Cobra el servicio
		boolean paidService = payService(ride.getAccountId(), totalPrice);
		if (!paidService) {
			return ResponseEntity.badRequest().build();
		}
		
		//Guarda los cambios
		return ResponseEntity.ok(ridesRepository.save(ride));
	}
	*/
	
	private boolean checkScooterInStop(int scooterId) {
		String url = "http://localhost:8888/scooters/" + scooterId + "/currentStop";
		String response = getOk(url);
		return response != null;
	}

	private boolean payService(int accountId, double price) {
		String url = "http://localhost:8081/accounts/" + accountId + "/payService";
		String json = convertToJson(new PaymentDto(price));
		
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        
		HttpClient httpClient = HttpClient.newHttpClient();
		
		try {
		    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		    if (response.statusCode() == 200) {
		    	return true;
		    }
        } catch (Exception e) {
            return false;
        }
		return false;
	}
	
	private String getOk(String url) { 
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
	}
	
	private Ride convertToEntity(StartRideDto dto, int userId) {
		return new Ride(LocalDateTime.now(), userId, dto.getScooterId());
	}
	
	private String convertToJson(PaymentDto dto) {
        ObjectMapper objectMapper = new ObjectMapper();
         try {
             return objectMapper.writeValueAsString(dto);
         } catch (JsonProcessingException e) {
             e.printStackTrace();
         }
        return "";
	}

	public ResponseEntity<List<ScooterWithTimeDto>> getScootersOrderedByTotalTime() {
	    List<Ride> rides = ridesRepository.findAll();
	    Map<Integer, Long> scooterTotalTimeMap = new HashMap<>();
	    for (Ride ride : rides) {
	    	Integer scooterId = ride.getScooterId();
			long rideDurationInSeconds = calculateDurationInSeconds(ride.getStartTime().toLocalTime(), ride.getEndTime().toLocalTime());
			long currentScooterTime = scooterTotalTimeMap.getOrDefault(scooterId, 0L);
			scooterTotalTimeMap.put(ride.getScooterId(), rideDurationInSeconds + currentScooterTime);
		}
	    List<ScooterWithTimeDto> scootersDtos = new ArrayList<>();
	    for (Map.Entry<Integer, Long> entry : scooterTotalTimeMap.entrySet()) {
			scootersDtos.add(new ScooterWithTimeDto(entry.getKey(), entry.getValue()));
		}
	    
	    return ResponseEntity.ok(scootersDtos);
	}

	public ResponseEntity<List<ScooterWithTimeDto>> getScootersOrderedByTotalTimeWithoutPauses() {
	    List<Ride> rides = ridesRepository.findAll();
	    Map<Integer, Long> scooterTotalTimeMap = new HashMap<>();
	    
	    for (Ride ride : rides) {
	    	Integer scooterId = ride.getScooterId();
	    	long rideDurationInSeconds = calculateDurationInSeconds(ride.getStartTime().toLocalTime(), ride.getEndTime().toLocalTime());
			long currentScooterTime = scooterTotalTimeMap.getOrDefault(scooterId, 0L);
			
			long totalPauseSeconds = 0;
			for (Pause pause : ride.getPauses()) {
				if (pause.getEndTime() != null) {
					long pauseDurationInSeconds = calculateDurationInSeconds(pause.getStartTime(), pause.getEndTime());
					totalPauseSeconds += pauseDurationInSeconds;
				}
			}
			
			scooterTotalTimeMap.put(ride.getScooterId(), currentScooterTime + rideDurationInSeconds - totalPauseSeconds);
		}
	    
	    List<ScooterWithTimeDto> scootersDtos = new ArrayList<>();
	    for (Map.Entry<Integer, Long> entry : scooterTotalTimeMap.entrySet()) {
			scootersDtos.add(new ScooterWithTimeDto(entry.getKey(), entry.getValue()));
		}
	    return ResponseEntity.ok(scootersDtos);
	}
	
	private long calculateDurationInSeconds(LocalTime startTime, LocalTime endTime) {
		Duration duration = Duration.between(startTime, endTime);
		return duration.getSeconds();
	}
}