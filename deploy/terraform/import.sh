#!/bin/sh

function import {
  terraform \
    import \
    -var="onepassword_token=$ONEPASSWORD_TOKEN"\
    "$1" "$2"
}

function plan {
  terraform plan \
    -var "onepassword_token=$ONEPASSWORD_TOKEN"
}

import "keycloak_openid_client.market_tracker_portfolio_service_dev" "apps-dev/0e5fca6f-3807-4909-ab61-8357336cf9a3"
import "keycloak_openid_client.market_tracker_portfolio_service_prod" "apps-prod/118ec733-712e-4320-bd1c-7816c9403fb3"

import "keycloak_role.market_tracker_portfolio_service_access_role_dev" "apps-dev/71f18019-87b3-4240-8920-bd472738b1e7"
import "keycloak_role.market_tracker_portfolio_service_access_role_prod" "apps-prod/68f971ba-68d8-42ba-84e1-f2b9bf46d468"

import "keycloak_role.market_tracker_all_access_dev" "apps-dev/"
import "keycloak_role.market_tracker_all_access_prod" "apps-prod/"

plan