package io.craigmiller160.markettracker.portfolio.web.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad Request")
class MissingParameterException(paramName: String, cause: Throwable? = null) :
    RuntimeException("Missing required parameter: $paramName", cause)
