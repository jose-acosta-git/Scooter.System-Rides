package rides.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import rides.dtos.EndRideDto;
import rides.dtos.PaymentDto;
import rides.dtos.StartRideDto;
import rides.model.Pause;
import rides.model.Ride;
import rides.repositories.RidesRepository;


@Service
public class RidesService {
	
	@Autowired
	private RidesRepository ridesRepository;
	
	private HttpClient client = HttpClient.newHttpClient();

	public ResponseEntity<Ride> startRide(StartRideDto dto) {
		String accountResponse = getOk("http://localhost:8081/accounts/" + dto.getAccountId());
		String scooterResponse = getOk("http://localhost:8888/scooters/" + dto.getScooterId());
		if (accountResponse == null || scooterResponse == null) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(ridesRepository.save(convertToEntity(dto)));

	}
	
	public ResponseEntity<Ride> endRide(int rideId, EndRideDto dto) {
		Optional<Ride> optionalRide = ridesRepository.findById(rideId);
		if (!optionalRide.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Ride ride = optionalRide.get();
		LocalDateTime endTime = LocalDateTime.now();
		Duration duration = Duration.between(ride.getStartTime(), endTime);
		long seconds = duration.getSeconds();
		String standardPrice = getOk("http://localhost:9090/fares/currentStandardPrice");
		String extendedPausePrice = getOk("http://localhost:9090/fares/currentExtendedPausePrice");
		if (standardPrice == null || extendedPausePrice == null) {
			return ResponseEntity.badRequest().build();
		}
		double totalPrice = 0;
		double standardValue = Double.valueOf(standardPrice);
		double extendedPauseValue = Double.valueOf(extendedPausePrice);
		
		for (Pause pause : ride.getPauses()) {
			Duration pauseDuration = Duration.between(pause.getStartTime(), pause.getEndTime());
			long pauseSeconds = pauseDuration.getSeconds();
			if (pauseSeconds > 900) {
				Duration extendedPriceDuration = Duration.between(ride.getEndTime(), pause.getEndTime());
				long extendedPriceSeconds = extendedPriceDuration.getSeconds();
				seconds -= extendedPriceSeconds;
				totalPrice += extendedPriceSeconds * extendedPauseValue;
			}
		}
		
		totalPrice += seconds * standardValue;
		
		ride.setEndTime(endTime);
		ride.setDistance(dto.getDistance());
		ride.setPrice(totalPrice);
		
		boolean paidService = payService(ride.getAccountId(), totalPrice);
		if (!paidService) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(ridesRepository.save(ride));
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
	
	private Ride convertToEntity(StartRideDto dto) {
		return new Ride(LocalDateTime.now(), dto.getAccountId(), dto.getScooterId());
	}
	
	private String convertToJson(PaymentDto dto) {
        // Implement JSON serialization logic using your preferred library (e.g., Jackson, Gson)

        ObjectMapper objectMapper = new ObjectMapper();
         try {
             return objectMapper.writeValueAsString(dto);
         } catch (JsonProcessingException e) {
             e.printStackTrace();
         }
        return "";
	}
}