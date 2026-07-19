.PHONY: help docker-restart openapi test test-smoke test-integration

COMPOSE := docker compose
API_HOST ?= http://localhost:8080
OUTPUT_FILE ?= openapi.json

help:
	@echo "Available targets:"
	@echo "  docker-restart  Stop stack, wipe DB volume, remove app image, rebuild and start"
	@echo "  openapi         Export OpenAPI spec from a running API (default: openapi.json)"
	@echo "                  Requires the app to be up. Override: make openapi API_HOST=... OUTPUT_FILE=..."
	@echo "  test            Alias for test-smoke (quiet progress + summary at end)"
	@echo "  test-smoke      Run full suite with AI stubs (no external OpenAI calls)"
	@echo "  test-integration Run full suite against real OpenAI API (requires OPEN_AI_KEY in .env.test)"

docker-restart:
	$(COMPOSE) down -v --rmi local --remove-orphans
	$(COMPOSE) up -d --build

openapi:
	API_HOST=$(API_HOST) OUTPUT_FILE=$(OUTPUT_FILE) ./export-openapi-spec.sh

test: test-smoke

test-smoke:
	./scripts/run-tests.sh smoke

test-integration:
	./scripts/run-tests.sh integration
