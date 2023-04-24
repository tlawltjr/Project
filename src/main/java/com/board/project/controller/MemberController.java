package com.board.project.controller;

import com.board.project.dto.MemberAdapter;
import com.board.project.dto.MemberDTO;
import com.board.project.entity.Member;
import com.board.project.service.LoginService;
import com.board.project.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/whitelabel")
public class MemberController {

    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;

    private final LoginService loginService;


    @GetMapping("/join")
    public String join(@ModelAttribute MemberDTO memberDTO) {
       return "whitelabel/join";
    }
    
    @PostMapping("/join")
    public String joinProc(@ModelAttribute MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
       log.info("회원가입 요청");
       Member number = memberService.dtoToEntity(memberDTO);
       
       redirectAttributes.addFlashAttribute("msg",number);
       return "redirect:/whitelabel/loginPage";
       
    }

    @GetMapping("/myPage")
    public String getMyPage(@AuthenticationPrincipal MemberAdapter memberAdapter, Model model) {

        Member member = memberAdapter.getMember();



        model.addAttribute("member", member);


        return "whitelabel/myPage";
    }
    @PostMapping("/myPage")
    public String myPage(Member member, Model model) {

        memberService.updateMember(member);


        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(member.getLoginId(), member.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("member",member);

        return "redirect:/whitelabel/myPage";
    }
}