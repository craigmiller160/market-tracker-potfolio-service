locals {
  access_role_common = {
    name = "access"
  }
}

data "keycloak_openid_client" "market_tracker_api_dev" {
  realm_id  = data.keycloak_realm.apps_dev.id
  client_id = "market-tracker-api"
}

data "keycloak_openid_client" "market_tracker_api_prod" {
  realm_id  = data.keycloak_realm.apps_prod.id
  client_id = "market-tracker-api"
}

data "keycloak_role" "market_tracker_api_access_role_dev" {
  realm_id  = data.keycloak_realm.apps_dev.id
  name = local.access_role_common.name
}

data "keycloak_role" "market_tracker_api_access_role_prod" {
  realm_id  = data.keycloak_realm.apps_prod.id
  name = local.access_role_common.name
}

resource "keycloak_role" "market_tracker_portfolio_service_access_role_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = keycloak_openid_client.market_tracker_portfolio_service_dev.id
  name = local.access_role_common.name
}

resource "keycloak_role" "market_tracker_portfolio_service_access_role_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = keycloak_openid_client.market_tracker_portfolio_service_prod.id
  name = local.access_role_common.name
}


#resource "keycloak_role" "admin_role" {
#  realm_id        = keycloak_realm.realm.id
#  name            = "admin"
#  composite_roles = [
#    keycloak_role.create_role.id,
#    keycloak_role.read_role.id,
#    keycloak_role.update_role.id,
#    keycloak_role.delete_role.id,
#    keycloak_role.client_role.id,
#  ]
#
#  attributes = {
#    key = "value"
#  }
#}