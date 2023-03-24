package io.craigmiller160.markettracker.portfolio.domain.sql

import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

@Component class SqlLoader(private val resourceLoader: ResourceLoader) {}
