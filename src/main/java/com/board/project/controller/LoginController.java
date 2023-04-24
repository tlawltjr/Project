package com.board.project.controller;

import com.board.project.dto.LoginDTO;
import com.board.project.dto.MemberAdapter;
import com.board.project.dto.MemberDTO;
import com.board.project.entity.Member;
import com.board.project.repository.MemberRepository;
import com.board.project.service.LoginService;

import lombok.RequiredArgsConstructor;

import org.json.simple.parser.ParseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@RequestMapping("/whitelabel")
@RequiredArgsConstructor
public class LoginController {
	private final PasswordEncoder passwordEncoder;

	private final MemberRepository memberRepository;

	// Service
	private final LoginService loginService;

	// 아래의 모든 매핑에서는 여러분이 응요할 수 잇도록 요청 패턴과 이에 대응하는
	// Viewer 를 지금까지 했던것과는 틀리게 처리 하였으니, 필요시 응용하세요.
	@GetMapping("/loginPage")
	public String loginForm() {
		// Viewer 매핑

		return "whitelabel/loginPage";
	}

	@PostMapping("/loginPage")
	public String login(Member member) {

		UserDetails members = loginService.loadUserByUsername(member.getLoginId());
		
		if (members.getUsername().equals(member.getLoginId())) {
			return "redirect:/whitelabel/";
		} else if (!members.getUsername().equals(member.getLoginId())) {

			return "redirect:/whitelabel/loginPage";
		}

		return null;

	}

	@GetMapping("/redirectKakao")
	public String kakaoMember(@RequestParam("code") String code) throws ParseException {

		String test = loginService.getKakaoAccessToken(code);

		loginService.getKakaoToken(test);

		return "redirect:/whitelabel/index";
	}

	@GetMapping("/naverLogin")
	public String a(HttpSession session) {
		String clientId = "qJbe4seUigejowTfnIsA";
		String redirectUri = "http://localhost:8080/whitelabel/redirectNaver";

		String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
		apiURL += "&client_id=" + clientId;
		apiURL += "&redirect_uri=" + redirectUri;
		apiURL += "&state=" + generateRandomString();

		session.setAttribute("state", apiURL);

		return "redirect:" + apiURL;
	}

	private String generateRandomString() {
		// TODO Auto-generated method stub

		SecureRandom random = new SecureRandom();

		byte[] bytes = new byte[20];
		random.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	@GetMapping("/redirectNaver")
	public String d() {

		return "redirect:/whitelabel/naverPars";

	}

	@GetMapping("/naverPars")
	public void asd() {

	}


}