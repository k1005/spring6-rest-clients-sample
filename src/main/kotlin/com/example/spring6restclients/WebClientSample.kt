package com.example.spring6restclients

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

/**
 * WebClient 사용을 위해서는 Reactive 설정이 필요하다. _build.gradle_ 내에 아래 디펜던시를 추가하자.
 * implementation 'org.springframework.boot:spring-boot-starter-webflux'
 */
@Configuration
class WebClientSample {

    @Bean
    fun webClient() = WebClient.builder().baseUrl("https://open.er-api.com").build()

    @Bean
    fun webClientRunner(webClient: WebClient) = ApplicationRunner {
        val res = webClient.get().uri("/v6/latest").retrieve().bodyToMono<Map<String, Any>>().block()
        val rates = res?.get("rates") as Map<*, *>
        println("${rates["KRW"]} from WebClient")
    }
}
