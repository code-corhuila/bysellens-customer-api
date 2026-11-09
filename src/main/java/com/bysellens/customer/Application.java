package com.bysellens.customer;
import org.springframework.boot.SpringApplication;import org.springframework.boot.autoconfigure.SpringBootApplication;import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication(scanBasePackages={"com.bysellens.customer","com.bysellens.common"}) @EnableScheduling
public class Application {public static void main(String[] args){SpringApplication.run(Application.class,args);}}