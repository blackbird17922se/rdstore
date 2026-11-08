variable "resource_group_name_prefix" {
  description = "Prefix for the resource group name"
  type        = string
  default     = "rg-rdstore"
}

variable "resource_group_location" {
  description = "Azure region for RDStore resources"
  type        = string
  default     = "East US 2"
}

variable "container_app_env_name" {
  description = "Name of the Azure Container App Environment"
  type        = string
  default     = "rdstore-env"
}