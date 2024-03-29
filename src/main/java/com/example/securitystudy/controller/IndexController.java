package com.example.securitystudy.controller;

import com.example.securitystudy.config.auth.PrincipalDetails;
import com.example.securitystudy.model.User;
import com.example.securitystudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(
            Authentication authentication,
            @AuthenticationPrincipal PrincipalDetails userDetails) { // DI(의존성 주입)
        System.out.println("/test/login ========================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        // 두가지 방법이 있다
        System.out.println("authentication: " + principalDetails.getUser());
        System.out.println("userDetails: " + userDetails.getUser());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oAuth) { // DI(의존성 주입)
        System.out.println("/test/oauth/login ========================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        System.out.println("authentication: " + oAuth2User.getAttributes());
        System.out.println("oauth2User: " + oAuth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }

    @GetMapping({"", "/"})
    public String index() {
        // 뷰리졸버 설정: templates(prefix), mustache(suffix) 생략가능!!
        return "index"; // src/main/resources/templates/index.mustache
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails:" + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    // 스프링 시큐리티 해당주소를 낚아채버리네요!!
    // SecurityConfig 파일 생성후 작동안함.
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @GetMapping("/join")
    public @ResponseBody String join() {
        return "join";
    }
    @PostMapping("/join")
    public String join(User user) {
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        // 회원가입 잘됨. 비밀번호:1234 => 시큐리티로 로그인을 할 수 있음.
        // 이유는 패스워드가 암호화가 안되었기 때문!
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    // @PostAuthorize() pre, post 잘 안쓴다.
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터정보";
    }



}
