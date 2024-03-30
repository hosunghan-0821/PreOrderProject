package com.preorder.global.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ApplicationOptionConfig {
    private boolean SMSService=false;

}
