terraform {
  backend "azurerm" {
    resource_group_name  = "rg-rdstore-tfstate"
    storage_account_name = "rdstoretfstate17922"
    container_name       = "tfstate"
    key                  = "rdstore.tfstate"
    use_azuread_auth     = true
  }
}