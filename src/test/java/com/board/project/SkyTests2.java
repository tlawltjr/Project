package com.board.project;

import static org.assertj.core.api.Assertions.entry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import javax.net.ssl.HttpsURLConnection;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class SkyTests2 {

	private static final String API_URL = "https://kapi.kakao.com/v1/payment/ready";
	private static final String APP_ADMIN_KEY = "2b919d7c5c41ed7dad4a4bbaafbe6469";
	@Test
	public void sky() throws Exception{
		

	        String cid = "TC0ONETIME";
	        String partnerOrderId = "partner_order_id";
	        String partnerUserId = "partner_user_id";
	        String itemName = "초코파이";
	        int quantity = 1;
	        int totalAmount = 2200;
	        int vatAmount = 200;
	        int taxFreeAmount = 0;
	        String approvalUrl = "https://developers.kakao.com/success";
	        String failUrl = "https://developers.kakao.com/fail";
	        String cancelUrl = "https://developers.kakao.com/cancel";

	        URL url = new URL(API_URL);
	        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

	        con.setRequestMethod("POST");
	        con.setRequestProperty("Authorization", "KakaoAK " + APP_ADMIN_KEY);
	        con.setDoOutput(true);

	        Map<String, String> params = new HashMap<>();
	        params.put("cid", cid);
	        params.put("partner_order_id", partnerOrderId);
	        params.put("partner_user_id", partnerUserId);
	        params.put("item_name", itemName);
	        params.put("quantity", String.valueOf(quantity));
	        params.put("total_amount", String.valueOf(totalAmount));
	        params.put("vat_amount", String.valueOf(vatAmount));
	        params.put("tax_free_amount", String.valueOf(taxFreeAmount));
	        params.put("approval_url", approvalUrl);
	        params.put("fail_url", failUrl);
	        params.put("cancel_url", cancelUrl);

	        StringJoiner sj = new StringJoiner("&");
	        for (Map.Entry<String, String> entry : params.entrySet()) {
	            sj.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
	                    + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
	        }

	        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
	        int length = out.length;

	        con.setFixedLengthStreamingMode(length);
	        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        con.connect();

	        try (OutputStream os = con.getOutputStream()) {
	            os.write(out);
	        }

	        int responseCode = con.getResponseCode();

	        if (responseCode == HttpsURLConnection.HTTP_OK) {
	            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();
	            System.out.println(response.toString());
	        } else {
	            System.out.println("Request failed with response code " + responseCode);
	        }
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
