package com.board.project.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.board.project.dto.MemberAdapter;
import com.board.project.entity.Member;
import com.board.project.entity.MemberRole;
import com.board.project.repository.MemberRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class LoginService implements UserDetailsService {

   private final MemberRepository memberRepository;

   private final static String NAVER_CLIENT_ID = "qJbe4seUigejowTfnIsA";
   private final static String NAVER_CLIENT_SECRET = "br_CsDOVPb";
   private final static String NAVER_CALLBACK_URL = "http://localhost:8080/whitelbel/redirectNaver";

   
   
   
   
   @Override
   public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

      Member member = memberRepository.findByLoginId(loginId).orElseThrow(()
              -> new UsernameNotFoundException("회원 정보가 없습니다."));

      System.out.println("여기는 로드유저" + member.toString());


      return new MemberAdapter(member);
   }
   
   
   public String getKakaoAccessToken(String code) {

	    RestTemplate restTemplate = new RestTemplate();

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

	    MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
	    map.add("grant_type", "authorization_code");
	    map.add("client_id", "99e84536ac6051b1ba62273501c9cd57");
	    map.add("redirect_uri", "http://localhost:8080/whitelabel/redirectKakao");
	    map.add("code", code);

	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

	    ResponseEntity<String> response = restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", request, String.class);

	    String accessToken = response.getBody().split("\"access_token\":\"")[1].split("\"")[0];
	    
	    if (response.getStatusCode() == HttpStatus.OK) {
	        return accessToken; // Access Token이 담긴 JSON 문자열 반환
	    } else {
	        return null;
	    }
	}
  
  public void getKakaoToken(String token) throws ParseException {
	   
	   RestTemplate restTemplate = new RestTemplate();
	   
	   System.out.println("getToken 서비스 token=="+token);

	   HttpHeaders headers = new HttpHeaders();
	   headers.setBearerAuth(token);
	   headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

	   HttpEntity<String> entity = new HttpEntity<>("body", headers);

	   ResponseEntity<String> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, entity, String.class);

	   String getBody = response.getBody();
	   
	   System.out.println("서비스 getBody=="+getBody);
	   
	   JSONParser parser = new JSONParser();
	   
	   JSONObject obj = (JSONObject) parser.parse(getBody);
	   
	   JSONObject properties = (JSONObject) obj.get("kakao_account");
	   
	   String email = (String) properties.get("email");
	   
	   Member member = Member.builder().email(email).fromSocial(true).loginId(email).role(MemberRole.ADMIN.getValue()).build();
	   
	   memberRepository.save(member);
	   
  }
  @GetMapping("/redirectNaver")
  public String getNaverAccessToken(String code) {

	   RestTemplate restTemplate = new RestTemplate();
      
      // 네이버 로그인 API 요청에 필요한 헤더 설정
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
      headers.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
      
      // 네이버 로그인 API 요청에 필요한 파라미터 설정
      MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
      map.add("grant_type", "authorization_code");
      map.add("client_id", NAVER_CLIENT_ID);
      map.add("client_secret", NAVER_CLIENT_SECRET);
      map.add("redirect_uri", NAVER_CALLBACK_URL);
      map.add("code", code);
      
      // 네이버 로그인 API 호출
      HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
      ResponseEntity<Map> response = restTemplate.exchange(
              "https://nid.naver.com/oauth2.0/token",
              HttpMethod.POST,
              entity,
              Map.class
      );
      
      // 로그인 토큰 값 반환
      Map<String, Object> responseBody = response.getBody();
      return responseBody.get("access_token").toString();
	}
}