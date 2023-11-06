package rides.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
		
		//Obtengo las tarifas actuales
		String standardPriceResponse = getOk("http://localhost:9090/fares/currentStandardPrice");
		String extendedPausePriceResponse = getOk("http://localhost:9090/fares/currentExtendedPausePrice");
		if (standardPriceResponse == null || extendedPausePriceResponse == null) {
			return ResponseEntity.badRequest().build();
		}

		//Inicializo variables
		Ride ride = optionalRide.get();
		LocalDateTime endTime = LocalDateTime.now();
		
		//Calculo el precio del viaje
		double totalPrice = 0;
		double standardPrice = Double.valueOf(standardPriceResponse);
		double extendedPausePrice = Double.valueOf(extendedPausePriceResponse);
		LocalTime higherRateStartTime = null;
		
		for (Pause pause : ride.getPauses()) {
			Duration pauseDuration = Duration.between(pause.getStartTime(), pause.getEndTime());
			long pauseSeconds = pauseDuration.getSeconds();
			
			//cambiar a 900
			if (pauseSeconds > 60) {
				//cambiar a 15
				LocalTime currentHigherRateStartTime = pause.getStartTime().plusMinutes(1);
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

		
		//Establezco los valores del viaje
		ride.setEndTime(endTime);
		ride.setDistance(dto.getDistance());
		ride.setPrice(totalPrice);
		
		//Cobro el servicio
		boolean paidService = payService(ride.getAccountId(), totalPrice);
		if (!paidService) {
			return ResponseEntity.badRequest().build();
		}
		
		//Guardo los cambios
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