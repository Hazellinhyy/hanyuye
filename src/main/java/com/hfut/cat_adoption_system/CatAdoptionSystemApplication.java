package com.hfut.cat_adoption_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan("com.hfut.cat_adoption_system.mapper")
@SpringBootApplication
public class CatAdoptionSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatAdoptionSystemApplication.class, args);
	}

}
