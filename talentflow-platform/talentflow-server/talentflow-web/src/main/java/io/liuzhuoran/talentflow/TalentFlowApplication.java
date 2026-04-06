package io.liuzhuoran.talentflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@MapperScan(basePackages = "io.liuzhuoran.talentflow.mapper")
@EnableScheduling
public class TalentFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TalentFlowApplication.class, args);
    }

}