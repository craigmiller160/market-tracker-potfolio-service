package io.craigmiller160.markettracker.portfolio.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import java.lang.RuntimeException
import java.time.Duration
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig(@Value("\${http-client.timeout-millis}") private val timeoutMillis: Int) {
  @Bean
  fun webClient(): WebClient =
      WebClient.builder()
          //          .filter(errorFilter())
          .clientConnector(ReactorClientHttpConnector(httpClient()))
          .build()

  // TODO cleanup
  private fun errorFilter(): ExchangeFilterFunction =
      ExchangeFilterFunction.ofResponseProcessor { res ->
        if (res.statusCode().is4xxClientError || res.statusCode().is5xxServerError) {
          // TODO need a better exception
          Mono.error(RuntimeException())
        } else {
          Mono.just(res)
        }
      }

  private fun httpClient(): HttpClient =
      HttpClient.create()
          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutMillis)
          .responseTimeout(Duration.ofMillis(timeoutMillis.toLong()))
          .doOnConnected { conn ->
            conn
                .addHandlerLast(ReadTimeoutHandler(timeoutMillis.toLong(), TimeUnit.MILLISECONDS))
                .addHandlerLast(WriteTimeoutHandler(timeoutMillis.toLong(), TimeUnit.MILLISECONDS))
          }
}
