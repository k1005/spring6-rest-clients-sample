package com.example.spring6restclients

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateSample {

    @Bean
    fun restTemplateRunner() = ApplicationRunner {
        val mapClass = Map::class.java as Class<Map<String, Any>>
        val res = RestTemplate().getForObject("https://open.er-api.com/v6/latest", mapClass)
        val rates = res?.get("rates") as Map<*, *>
        println("${rates["KRW"]} from RestTemplate")
    }

}
