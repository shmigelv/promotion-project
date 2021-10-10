package com.shmigel.promotionproject;

import com.shmigel.promotionproject.model.dto.LessonDetailsDTO;
import com.shmigel.promotionproject.repository.LessonRepository;
import com.shmigel.promotionproject.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collection;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
