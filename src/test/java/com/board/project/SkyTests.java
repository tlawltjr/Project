package com.board.project;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.board.project.dto.SkySearchResultDTO;


@SpringBootTest
public class SkyTests {

	
	@Test
	public void sky() throws Exception{
		
		String resultName = null;//airline 코드 json 변환 dto 저장용
		
		SkySearchResultDTO dto = null;
		
		HashMap<String, List<SkySearchResultDTO>> itinerary1 = null;
		
		List<SkySearchResultDTO> list = new ArrayList<>();
		List<HashMap<String, List<SkySearchResultDTO>>> listResult = new ArrayList<>();
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://priceline-com-provider.p.rapidapi.com/v2/flight/departures?"
						+ "adults=1"
						+ "&departure_date=2023-05-06"
						+ "&sid=iSiX639"
						+ "&results_per_page=10"
						+ "&number_of_itineraries=10"
						+ "&cabin_class=economy"
						+ "&origin_airport_code=ICN"
						+ "&destination_airport_code=ITM"))
				.header("X-RapidAPI-Key", "a6931c3c0bmshaab4694548eda51p11f7e6jsn1fe9ae29c474")
				.header("X-RapidAPI-Host", "priceline-com-provider.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
				HttpResponse<InputStream> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
				InputStream inputStream = response.body();
		// check if response is compressed with gzip
		if (response.headers().firstValue("Content-Encoding").orElse("").equalsIgnoreCase("gzip")) {
		    inputStream = new GZIPInputStream(inputStream);
		}

		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}

		String responseBody = result.toString(StandardCharsets.UTF_8);
		
		JSONParser parser = new JSONParser();
		
		JSONObject jObject = (JSONObject) parser.parse(responseBody);
		
		JSONObject content = (JSONObject) jObject.get("getAirFlightDepartures");
		
		JSONObject results = (JSONObject) content.get("results");
		
		JSONObject result_data = (JSONObject) results.get("result");
		
		JSONObject itinerary_data = (JSONObject) result_data.get("itinerary_data");
		
		System.out.println("itinerary size-==="+itinerary_data.size());
		
		Object viaCode;
		
		Object viaDateTime;
		
		for (int i = 0; i < itinerary_data.size(); i++) {
		    JSONObject itinerary = (JSONObject)itinerary_data.get("itinerary_" + i);
		    //price
		    JSONObject pricedetail = (JSONObject)itinerary.get("price_details");
		    Object display_total_fare_per_ticket = pricedetail.get("display_total_fare_per_ticket");
		    
		    //slice
		    JSONObject slice = (JSONObject)itinerary.get("slice_data");
		    
		    //slice_0
		    JSONObject sl = (JSONObject) slice.get("slice_0");
		    
		    //info
		    JSONObject infomation = (JSONObject) sl.get("info");
		    Object duration = infomation.get("duration");
		    
		    //airLine
		    JSONObject airline = (JSONObject) sl.get("airline");
		    Object airName = airline.get("code");
		    Object logo = airline.get("logo");
		    
		    //departure
		    JSONObject departure = (JSONObject) sl.get("departure");
		    JSONObject airport1 = (JSONObject) departure.get("airport");
		    Object depCode = airport1.get("code");
		    JSONObject dateTime1 = (JSONObject) departure.get("datetime");
		    Object dTime1 = dateTime1.get("date_time");
		    
		    //arrival
		    JSONObject arrival = (JSONObject) sl.get("arrival");
		    JSONObject airport2 = (JSONObject) arrival.get("airport");
		    Object arrCode = airport2.get("code");
		    JSONObject dateTime2 = (JSONObject) departure.get("datetime");
		    Object dTime2 = dateTime2.get("date_time");
		    
		  //flight
		    JSONObject flight = (JSONObject) sl.get("flight_data");
		    JSONObject flight_1 = (JSONObject) flight.get("flight_1");
		    
		    if(flight_1 == null) {
		    	viaCode=null;
		    	viaDateTime=null;
		    } else {
		    JSONObject viadeparture = (JSONObject) flight_1.get("departure");
		    JSONObject viairport = (JSONObject) viadeparture.get("airport");
		    viaCode = viairport.get("code");
		    
		    JSONObject viaTime = (JSONObject) viadeparture.get("datetime"); 
		    viaDateTime = viaTime.get("date_time");
		    
		    
		    }
		    
		    
		    ClassPathResource resource = new ClassPathResource("iata.json");
			
			String jsonStr = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
			
			String search = (String) airName; // 검색어
	        
	        JSONParser parser1 = new JSONParser();
	        
	        try {
	            JSONArray array = (JSONArray) parser1.parse(jsonStr); // JSON 파일 읽기
	            
	            for (Object obj : array) {
	                JSONObject airline1 = (JSONObject) obj;
	                String name = (String) airline1.get("영문명"); // "영문명" 가져오기
	                String iataCode = (String) airline1.get("IATA_CODE"); // "IATA_CODE" 가져오기
	                
	                if (iataCode.equals(search)) { // 검색어와 일치하는 경우
	                    resultName = name; // 결과 출력
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
		    
		    dto = SkySearchResultDTO.builder()
    			  .airLineName(resultName)
    			  .logo(String.valueOf(logo))
    			  .price(String.valueOf(display_total_fare_per_ticket))
    			  .duration(String.valueOf(duration))
    			  .depDate(String.valueOf(dTime1))
    			  .depCode(String.valueOf(depCode))
    			  .arrDate(String.valueOf(dTime2))
    			  .arrCode(String.valueOf(arrCode))
    			  .viaCode(String.valueOf(viaCode))
    			  .viaDateTime(String.valueOf(viaDateTime))
    			  .build();
		    	
		    
		    
		    list.add(dto);
		}
		
		
		
		System.out.println("list-----------"+list);
		
		
		
		
		
		
		
	}
	
//	@Test
//	public void original() throws Exception{
//		SkySearchResultDTO dto = null;
//
//		Map<String, List<SkySearchResultDTO>> itinerary1 = null;
//		
//		List<Map<String, List<SkySearchResultDTO>>> realList = new ArrayList<>();
//
//		HttpRequest request = HttpRequest.newBuilder()
//				.uri(URI.create("https://priceline-com-provider.p.rapidapi.com/v2/flight/departures?"
//						+ "adults="+adult
//						+ "&departure_date="+date 
//						+ "&sid=iSiX639" 
//						+ "&results_per_page=10"
//						+ "&number_of_itineraries=10"
//						+ "&cabin_class="+cabinClass
//						+ "&origin_airport_code="+origin 
//						+ "&destination_airport_code="+destination))
//				.header("X-RapidAPI-Key", "a6931c3c0bmshaab4694548eda51p11f7e6jsn1fe9ae29c474")
//				.header("X-RapidAPI-Host", "priceline-com-provider.p.rapidapi.com")
//				.method("GET", HttpRequest.BodyPublishers.noBody()).build();
//		HttpResponse<InputStream> response = HttpClient.newHttpClient().send(request,
//				HttpResponse.BodyHandlers.ofInputStream());
//		InputStream inputStream = response.body();
//		// check if response is compressed with gzip
//		if (response.headers().firstValue("Content-Encoding").orElse("").equalsIgnoreCase("gzip")) {
//			inputStream = new GZIPInputStream(inputStream);
//		}
//
//		ByteArrayOutputStream result = new ByteArrayOutputStream();
//		byte[] buffer = new byte[1024];
//		int length;
//		while ((length = inputStream.read(buffer)) != -1) {
//			result.write(buffer, 0, length);
//		}
//
//		String responseBody = result.toString(StandardCharsets.UTF_8);
//		
//		JSONParser parser = new JSONParser();
//
//		JSONObject jObject = (JSONObject) parser.parse(responseBody);
//
//		JSONObject content = (JSONObject) jObject.get("getAirFlightDepartures");
//
//		JSONObject results = (JSONObject) content.get("results");
//		
//		JSONObject result_data = (JSONObject) results.get("result");
//
//		JSONObject itinerary_data = (JSONObject) result_data.get("itinerary_data");
//
//		System.out.println("itinerary size-===" + itinerary_data.size());
//
//		String marketing_airline = null;
//		String dTime = null;
//		String code = null;
//		//double display_total_fare_per_ticket = 0;
//
//		for (int i = 0; i < itinerary_data.size(); i++) {
//			JSONObject itinerary = (JSONObject) itinerary_data.get("itinerary_" + i);
//			JSONObject pricedetail = (JSONObject) itinerary.get("price_details");
//			JSONObject slice = (JSONObject) itinerary.get("slice_data");
//			JSONObject sl = (JSONObject) slice.get("slice_0");
//			JSONObject flight_data = (JSONObject) sl.get("flight_data");
//
//			// 추출할 데이터가 있는 경우 이곳에서 처리합니다.
//			Object display_total_fare_per_ticket = pricedetail.get("display_total_fare_per_ticket");
//			
//			System.out.println("&&&&&&&&&&&&&&&&&&&" + i + "----토탈가격----" + display_total_fare_per_ticket);
//			
//			List<SkySearchResultDTO> list = new ArrayList<>();
//
//			for (int j = 0; j < flight_data.size(); j++) {
//				JSONObject flight = (JSONObject) flight_data.get("flight_" + j);
//				JSONObject arrival = (JSONObject) flight.get("arrival");
//				JSONObject dateTime = (JSONObject) arrival.get("datetime");
//
//				JSONObject departure = (JSONObject) flight.get("departure");
//				JSONObject airport = (JSONObject) departure.get("airport");
//				code = (String) airport.get("code");
//				System.out.println("code==-=-=-=-=" + code);
//
//				JSONObject info = (JSONObject) flight.get("info");
//				marketing_airline = (String) info.get("marketing_airline");
//				System.out.println(j + "====marketing_airline===" + marketing_airline);
//				dTime = (String) dateTime.get("date_time");
//				System.out.println(j + "=====flight=====" + dTime);
//
//				dto = SkySearchResultDTO.builder()
//						.airLine(marketing_airline)
//						.flightDate(dTime)
//						.price(String.valueOf(display_total_fare_per_ticket))
//						.code(code)
//						.build();
//
//				
//				list.add(dto);
//			}
//			itinerary1 = new HashMap<String, List<SkySearchResultDTO>>();
//			itinerary1.put("itinerary_"+i, list);
//			realList.add(itinerary1);
//			System.out.println("map1-----------" + itinerary1);
//
//		}
//		System.out.println("realList-=-=-="+realList);
//		//model.addAttribute("sky", realList);
//	
//		
//	}
}
