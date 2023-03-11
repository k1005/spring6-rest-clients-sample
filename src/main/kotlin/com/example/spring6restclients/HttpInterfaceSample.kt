package com.example.spring6restclients

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient

/**
 * spring 6 에서부터 사용 가능
 * https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#rest-http-interface
 */
@Configuration
class HttpInterfaceSample {

    @Bean
    fun httpInterfaceRunner(erApi: ErApi, webClient: WebClient) = ApplicationRunner {
        val factory = HttpServiceProxyFactory
            .builder(WebClientAdapter.forClient(webClient))
            .build()
        val service = factory.createClient<ErApi>()
        val res = service.latest() as Map<String, Map<String, Double>>
        println("${res["rates"]?.get("KRW")} from HttpInterface 1")

        val res2 = erApi.latest() as Map<String, Map<String, Double>>
        println("${res2["rates"]?.get("KRW")} from HttpInterface 2")
    }

    @Bean
    fun erApi(webClient: WebClient): ErApi = HttpServiceProxyFactory
        .builder(WebClientAdapter.forClient(webClient))
        .build()
        .createClient()

    interface ErApi {

        @GetExchange("/v6/latest")
        fun latest(): Map<String, Any>
    }
}