.PHONY: help docker-restart docker-e2e-up docker-e2e-down openapi test-smoke test-integration

COMPOSE := docker compose
COMPOSE_E2E := docker compose -f docker-compose.e2e.yml
API_HOST ?= http://localhost:8080
OUTPUT_FILE ?= openapi.json

help:
	@echo "Available targets:"
	@echo "  docker-restart   Stop stack, wipe DB volume, remove app image, rebuild and start"
	@echo "  docker-e2e-up    Start ephemeral E2E stack (OTP 123456, 4 worker accounts via Flyway V22)"
	@echo "  docker-e2e-down  Stop E2E stack"
	@echo "  openapi         Export OpenAPI spec from a running API (default: openapi.json)"
	@echo "                  Requires the app to be up. Override: make openapi API_HOST=... OUTPUT_FILE=..."
	@echo "  test-smoke      Run full suite with AI stubs (no external OpenAI calls)"
	@echo "  test-integration Run full suite against real OpenAI API (requires OPEN_AI_KEY in .env.test)"

docker-restart:
	$(COMPOSE) down -v --rmi local --remove-orphans
	$(COMPOSE) up -d --build

docker-e2e-up:
	$(COMPOSE_E2E) up -d --build

docker-e2e-down:
	$(COMPOSE_E2E) down --remove-orphans

openapi:
	API_HOST=$(API_HOST) OUTPUT_FILE=$(OUTPUT_FILE) ./export-openapi-spec.sh

test-smoke:
	./scripts/run-tests.sh smoke

test-integration:
	./scripts/run-tests.sh integration
