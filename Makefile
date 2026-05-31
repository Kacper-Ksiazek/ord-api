.PHONY: help docker-restart openapi

COMPOSE := docker compose
API_HOST ?= http://localhost:8080
OUTPUT_FILE ?= openapi.json

help:
	@echo "Available targets:"
	@echo "  docker-restart  Stop stack, wipe DB volume, remove app image, rebuild and start"
	@echo "  openapi         Export OpenAPI spec from a running API (default: openapi.json)"
	@echo "                  Requires the app to be up. Override: make openapi API_HOST=... OUTPUT_FILE=..."

docker-restart:
	$(COMPOSE) down -v --rmi local --remove-orphans
	$(COMPOSE) up -d --build

openapi:
	API_HOST=$(API_HOST) OUTPUT_FILE=$(OUTPUT_FILE) ./export-openapi-spec.sh
