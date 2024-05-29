package com.example.Messenger;

import com.cloudinary.Cloudinary;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Properties;

@SpringBootApplication
public class MessengerApplication {
	public static void main(String[] args) {
		SpringApplication.run(MessengerApplication.class, args);
	}
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
	@Bean
	public Cloudinary cloudinary(){
		return new Cloudinary(Map.of(
				"cloud_name", "dyxjtpcdu",
				"api_key", "598588667712299",
				"api_secret", "6ryph5s7fUyG2JoXPBTQz0C9vPg"
		));
	}

//	@Bean
//	public JavaMailSender javaMailSender(){
//		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//		mailSender.setHost("messenger.gmail.com");
//		mailSender.setPort(587);
//
//		mailSender.setUsername("kamil.gizatullin.03@gmail.com");
//		mailSender.setPassword("AlyaLove290522");
//
//		Properties prop = mailSender.getJavaMailProperties();
//		prop.put("mail.transport.protocol", "smtp");
//		prop.put("mail.smtp.auth", "true");
//		prop.put("mail.smtp.starttls.enable", "true");
//		prop.put("mail.debug", "true");
//
//		return mailSender;
//	}
}
