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

data "keycloak_role" "market_tracker_api_access_role_dev" {
  count = var.market_tracker_api_available
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = data.keycloak_openid_client.market_tracker_api_client_dev[0].id
  name = local.access_role_common.name
}

data "keycloak_role" "market_tracker_api_access_role_prod" {
  count = var.market_tracker_api_available
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = data.keycloak_openid_client.market_tracker_api_client_prod[0].id
  name = local.access_role_common.name
}

resource "keycloak_openid_client_service_account_role" "market_tracker_api_service_access_dev" {
  count = var.market_tracker_api_available
  realm_id = data.keycloak_realm.apps_dev.id
  service_account_user_id = keycloak_openid_client.market_tracker_portfolio_service_dev.service_account_user_id
  client_id = data.keycloak_openid_client.market_tracker_api_client_dev[0].id
  role = local.access_role_common.name
}

resource "keycloak_openid_client_service_account_role" "market_tracker_api_service_access_prod" {
  count = var.market_tracker_api_available
  realm_id = data.keycloak_realm.apps_prod.id
  service_account_user_id = keycloak_openid_client.market_tracker_portfolio_service_prod.service_account_user_id
  client_id = data.keycloak_openid_client.market_tracker_api_client_prod[0].id
  role = local.access_role_common.name
}