package com.github.hollykunge.security.sidecar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

/**
 * Created by 协同设计小组 on 2017/7/29.
 */
@EnableSidecar
@SpringBootApplication
public class SidercarBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(SidercarBootstrap.class, args);
    }
}
