package io.craigmiller160.markettracker.portfolio.web.swagger

import io.swagger.v3.oas.annotations.enums.ParameterIn

typealias SwaggerOperationBuilder = org.springdoc.core.fn.builders.operation.Builder

typealias SwaggerParameterBuilder = org.springdoc.core.fn.builders.parameter.Builder

typealias SwaggerResponseBuilder = org.springdoc.core.fn.builders.apiresponse.Builder

typealias SwaggerContentBuilder = org.springdoc.core.fn.builders.content.Builder

typealias SwaggerExampleBuilder = org.springdoc.core.fn.builders.exampleobject.Builder

typealias SwaggerSchemaBuilder = org.springdoc.core.fn.builders.schema.Builder

typealias SwaggerArraySchemaBuilder = org.springdoc.core.fn.builders.arrayschema.Builder

object SwaggerBuilders {
  fun pathParamBuilder(): SwaggerParameterBuilder =
      SwaggerParameterBuilder.parameterBuilder().`in`(ParameterIn.PATH)
  fun queryParamBuilder(): SwaggerParameterBuilder =
      SwaggerParameterBuilder.parameterBuilder().`in`(ParameterIn.QUERY)
  fun responseBuilder(): SwaggerResponseBuilder = SwaggerResponseBuilder.responseBuilder()
  fun contentBuilder(): SwaggerContentBuilder = SwaggerContentBuilder.contentBuilder()
  fun exampleBuilder(): SwaggerExampleBuilder = SwaggerExampleBuilder.exampleOjectBuilder()
  fun schemaBuilder(): SwaggerSchemaBuilder = SwaggerSchemaBuilder.schemaBuilder()
  fun arraySchemaBuilder(): SwaggerArraySchemaBuilder =
      SwaggerArraySchemaBuilder.arraySchemaBuilder()
}
