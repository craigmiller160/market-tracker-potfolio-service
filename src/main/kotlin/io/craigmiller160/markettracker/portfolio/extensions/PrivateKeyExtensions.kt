package io.craigmiller160.markettracker.portfolio.extensions

import java.util.Base64

const val BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----"
const val END_PRIVATE_KEY = "-----END PRIVATE KEY-----"

fun String.decodePrivateKeyPem(): ByteArray =
    replace(BEGIN_PRIVATE_KEY, "").replace(END_PRIVATE_KEY, "").replace(Regex("\\s+"), "").let {
      Base64.getDecoder().decode(it)
    }
