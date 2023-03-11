package io.craigmiller160.markettracker.portfolio

// fun main() {
//  // https://developers.google.com/identity/protocols/oauth2/service-account#httprest
//  val accountPath =
//      Paths.get(
//          System.getProperty("user.home"), "Downloads", "market-tracker-380317-fdda285edeac.json")
//  val objectMapper =
//      jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
//  val account = objectMapper.readValue(accountPath.readText(), Account::class.java)
//
//  val nowUtc = ZonedDateTime.now(ZoneId.of("UTC"))
//
//  val claims =
//      mapOf(
//          "sub" to account.clientEmail,
//          "iss" to account.clientEmail,
//          "scope" to "https://www.googleapis.com/auth/spreadsheets.readonly",
//          "aud" to account.tokenUri,
//          "iat" to nowUtc.toEpochSecond(),
//          "exp" to nowUtc.plusMinutes(30).toEpochSecond())
//
//  val claimsSet = JWTClaimsSet.parse(claims)
//  val header = JWSHeader.Builder(JWSAlgorithm.RS256).keyID(account.privateKeyId).build()
//
//  val privateKeyBytes =
//      account.privateKey
//          .replace("-----BEGIN PRIVATE KEY-----", "")
//          .replace("-----END PRIVATE KEY-----", "")
//          .replace(Regex("\\s+"), "")
//          .let { Base64.getDecoder().decode(it) }
//  val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
//  val privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec)
//
//  val signer = RSASSASigner(privateKey)
//  val signedJwt = SignedJWT(header, claimsSet)
//  signedJwt.sign(signer)
//  println(signedJwt.serialize())
// }
//
// data class Account(
//    val type: String,
//    val projectId: String,
//    val privateKeyId: String,
//    val privateKey: String,
//    val clientEmail: String,
//    val clientId: String,
//    val authUri: String,
//    val tokenUri: String,
//    val authProviderX509CertUrl: String,
//    val clientX509CertUrl: String
// )
