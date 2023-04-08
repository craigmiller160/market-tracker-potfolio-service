package io.craigmiller160.markettracker.portfolio.domain.rowmappers

import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata

class MockRow(private val map: Map<String, Any?>) : Row {
  override fun <T : Any?> get(index: Int, type: Class<T>): T? {
    TODO("Not yet implemented")
  }

  override fun <T : Any?> get(name: String, type: Class<T>): T? {
    if (!map.containsKey(name)) {
      throw NoSuchElementException("Column name $name does not exist")
    }
    val value = map[name] ?: return null
    if (!type.isAssignableFrom(value.javaClass)) {
      throw ClassCastException("Invalid type: ${type.name}")
    }
    return value as T
  }

  override fun getMetadata(): RowMetadata {
    TODO("Not yet implemented")
  }
}
