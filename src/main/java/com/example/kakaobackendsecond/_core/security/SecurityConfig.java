package com.example.kakaobackendsecond._core.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j // Lombok의 로거, 로그를 생성하기 위해 사용합니다
@RequiredArgsConstructor // 모든 final 필드와 @NonNull이 붙은 필드에 대한 생성자를 생성합니다
@Configuration // 스프링 IOC Container에게 해당 클래스가 Bean 구성 클래스임을 알려줍니다
public class SecurityConfig {
	@Bean
	// 패스워드 인코딩에 사용될 PasswordEncoder 빈을 생성합니다.
	// createDelegatingPasswordEncoder 메서드를 통해 여러 PasswordEncoder 중 적합한 것을 선택하여
	// 사용합니다.
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	// 인증 매니저를 빈으로 생성합니다. 인증 매니저는 사용자가 제공한 자격 증명을 검증하는 데 사용됩니다.
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	// HTTP 보안을 구성하기 위한 커스텀 필터 매니저를 정의합니다.
	public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
		@Override
		public void configure(HttpSecurity builder) throws Exception {
			AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
			// JWTAuthenticationFilter를 HTTP 보안 필터에 추가합니다.
			builder.addFilter(new JWTAuthenticationFilter(authenticationManager));
			super.configure(builder);
		}
	}


	// CORS 설정을 정의하는 메소드입니다.
	public CorsConfigurationSource configurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedHeader("*"); // 모든 헤더를 허용합니다.
		configuration.addAllowedMethod("*"); // 모든 HTTP 메서드를 허용합니다.
		configuration.addAllowedOriginPattern("*"); // 모든 원본을 허용합니다.
		configuration.setAllowCredentials(true); // 쿠키 사용을 허용합니다.
		configuration.addExposedHeader("Authorization"); // 'Authorization' 헤더를 노출합니다.

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정을 적용합니다.

		return source;
	}
}

