# spring6-rest-clients-sample

spring6 (spring-boot 3) 에서 활용 가능한 3가지 Rest API 활용 방법을 확인해 보자.
환율을 공개하는 API [https://open.er-api.com/v6/latest](`https://open.er-api.com/v6/latest`)를 통해 한국 원화 환율을 확인해 보는 샘플을 작성해 본다.

## RestTemplate

이미 많은 사람들이 사용하고 있는 RestTemplate 이다. 가장 단순하고 사용 방법도 가장 심플하다.

```kotlin
val mapClass = Map::class.java as Class<Map<String, Any>>
val res = RestTemplate().getForObject("https://open.er-api.com/v6/latest", mapClass)
val rates = res?.get("rates") as Map<*, *>
println("${rates["KRW"]} from RestTemplate")
```

## WebClient

많은 사람들이 처음 `WebClient` 등장에 환호 했다고 전해진다.
그 이유는 WebFlux 를 활용하는 사람들이 Reactive 스타일의 Rest Client 등장이었기 때문이라고 한다.

우선 spring-boot 기준으로 webflux 디펜던시를 추가해 주어야 `WebClient` 활용이 가능하다.

```gradle
// build.gradle
...
dependencies {
    ...
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    ...
}
...
```

```kotlin
val webClient = WebClient.builder().baseUrl("https://open.er-api.com").build()
val res = webClient.get().uri("/v6/latest").retrieve().bodyToMono<Map<String, Any>>().block()
val rates = res?.get("rates") as Map<*, *>
println("${rates["KRW"]} from WebClient")
```

이 예제는 간단한 사용을 위해 `.block()`을 사용해 `Reactive` 사용을 제거한 버전이다.
제대로 사용하기 위해서는 `Mono` 혹은 `Flux` 형태로 받아 사용해야 하지만 그에 대한 내용은 별개로 취급하자.

## HttpInterface

이 방법은 `WebClient`를 확장해 사용하는 방법이다.

> 좀 더 자세한 내용은 [spring 공식 문서](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#rest-http-interface)를 통해 확인할 수 있다.

우선 서비스를 등록하고자 하는 인터페이스를 생성해야 한다.

```kotlin
interface ErApi {

    @GetExchange("/v6/latest")
    fun latest(): Map<String, Any>
}
```

이제 인터페이스를 통해 실제 서비스를 생성서 활용하는 방법이다.

```kotlin
val factory = HttpServiceProxyFactory
    .builder(WebClientAdapter.forClient(webClient))
    .build()
val service = factory.createClient<ErApi>()
val res = service.latest() as Map<String, Map<String, Double>>
println("${res["rates"]?.get("KRW")} from HttpInterface 1")
```

하지만 이렇게 복잡하게 하려면 그냥 `WebClient`를 바로 생성해 사용하는게 나을 것 같다.
이건 이렇게 바로 사용하기 위한게 아니라 `@Bean` 으로 서비스를 등록해 `Reactive`하게 사용하기 위함이다.

> 복잡한 Reactive 처리과정은 모두 spring 에게 맡기기 위한 방법이다.
> 나중에 업그레이드를 통해 spring-boot 혹은 spring.framework 에서 자동화 해 줄 것을 믿고 있다.

다시 본론으로 돌아와서 앞서 작성한 `ErApi` 인터페이스를 서비스로 만들어 `@Bean`으로 등록하자.
물론 `@Configuration` 선언된 클래스 안에서 선언되어야 한다.

```kotlin
@Bean
fun erApi(webClient: WebClient): ErApi = HttpServiceProxyFactory
    .builder(WebClientAdapter.forClient(webClient))
    .build()
    .createClient()
```

이렇게 하면 `@Component` 등의 스프링 주입을 받을 수 있는 클래스에서 해당 서비스를 주입받아 사용할 수 있다.

```kotlin
@Bean
fun runner(erApi: ErApi) = ApplicationRunner {
    val res = erApi.latest() as Map<String, Map<String, Double>>
    println("${res["rates"]?.get("KRW")} from HttpInterface 1")
}
```

---

본 포스트를 작성하기 위한 샘플 소스코드는 [GitHub 저장소](https://github.com/k1005/spring6-rest-clients-sample)에서 확인할 수 있다.
