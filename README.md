# market-tracker-portfolio-service

## A Note on Terraform

In addition to the `$ONEPASSWORD_TOKEN` needing to be available as an OS environment variable, this application expects that the `market-tracker-api` application is already configured in Keycloak via terraform. This allows for the composite role to be put together.

## Service Account

The `market-tracker-service-account.json` is stored in 1Password.