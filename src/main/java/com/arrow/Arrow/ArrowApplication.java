package com.arrow.Arrow;

import com.arrow.Arrow.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@ComponentScan("com.teer")
@SpringBootApplication
@ServletComponentScan
//@EnableJpaRepositories(basePackages = "com.teer.Teer.repository.UserRepository.java")
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class ArrowApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArrowApplication.class, args);
	}

}
