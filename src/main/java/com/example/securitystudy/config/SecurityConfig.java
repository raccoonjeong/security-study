package com.example.securitystudy.config;

import com.example.securitystudy.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize, postAuthorize 어노테이션 활성화
public class SecurityConfig {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;
    // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable);
        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")

                        .anyRequest().permitAll()
        );
        http.formLogin(form -> form.loginPage("/loginForm")
                .loginProcessingUrl("/login") // login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해줍니다.
                .defaultSuccessUrl("/")
                .usernameParameter("username")
                .passwordParameter("password"))
                ;
        // 구글 로그인이 완료된 뒤의 후처리가 필요함
        // Tip. 코드X, (액세스토큰 + 사용자프로필정보 O)

        // 1. 코드받기(인증)
        // 2. 액세스토큰(권한)
        // 3. 사용자프로필 정보를 가져옴
        // 4-1. 그 정도를 토대로 회원가입 자동으로 진행시키기도 함
        // 4-2. (이메일, 전화번호, 이름, 아이디) 쇼핑몰 -> (집주소), 백화점몰 -> (vip등급, 일반등급)

        http.oauth2Login(o -> o
                .loginPage("/loginForm")
                .userInfoEndpoint(u -> u
                        .userService(principalOauth2UserService))
        );


//                .loginProcessingUrl("/login")
//                .defaultSuccessUrl("/")
//                .permitAll())
//                .httpBasic(httpBasic -> httpBasic.disable());
        return http.build();
    }
}
