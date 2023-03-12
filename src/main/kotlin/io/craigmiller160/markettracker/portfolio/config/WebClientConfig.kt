package io.craigmiller160.markettracker.portfolio.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import java.time.Duration
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig(@Value("\${http-client.timeout-millis}") private val timeoutMillis: Int) {
  @Bean
  fun webClient(): WebClient {
    val httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMillis)
            .responseTimeout(Duration.ofMillis(timeoutMillis.toLong()))
            .doOnConnected { conn ->
              conn
                  .addHandlerLast(ReadTimeoutHandler(timeoutMillis.toLong(), TimeUnit.MILLISECONDS))
                  .addHandlerLast(
                      WriteTimeoutHandler(timeoutMillis.toLong(), TimeUnit.MILLISECONDS))
            }
    return WebClient.builder().clientConnector(ReactorClientHttpConnector(httpClient)).build()
  }
}
