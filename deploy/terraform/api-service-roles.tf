data "keycloak_openid_client" "market_tracker_api_client_dev" {
  count = var.market_tracker_api_available
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = "market-tracker-api"
}

data "keycloak_openid_client" "market_tracker_api_client_prod" {
  count = var.market_tracker_api_available
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = "market-tracker-api"
}

