.PHONY: help status db-up db-wipe run restart stop openapi test test-live \
	dev-db dev dev-refresh dev-stop test-smoke test-integration

COMPOSE := docker compose
ORD_FRONTEND_DIR ?= $(HOME)/workspace/ord-frontend
API_HOST ?= http://localhost:8080
OUTPUT_FILE ?= openapi.json

.DEFAULT_GOAL := help

help:
	@echo "Available targets:"
	@echo ""
	@echo "🔍 Status:"
	@echo "  status          Show docker / api / front / storybook status"
	@echo ""
	@echo "💻 Native API (DB in Docker, JVM on host):"
	@echo "  db-up           Start Postgres (Docker)"
	@echo "  db-wipe         Wipe DB volume and restart Postgres"
	@echo "  run             Start native API (fast local dev)"
	@echo "  restart         Recompile and restart native API"
	@echo "  stop            Stop native API process"
	@echo ""
	@echo "🧪 Tests:"
	@echo "  test            Controller tests with AI stubs (CI default)"
	@echo "  test-live       Same suite with real OpenAI (.env.test)"
	@echo ""
	@echo "📄 OpenAPI:"
	@echo "  openapi         Export OpenAPI spec (API must be running)"
	@echo ""
	@echo "Override: make openapi API_HOST=... OUTPUT_FILE=..."
	@echo "E2E stack: use ord-ops (make e2e-up)"

status:
	ORD_API_DIR=$(CURDIR) ORD_FRONTEND_DIR=$(ORD_FRONTEND_DIR) ./scripts/dev-status.sh

db-up:
	./scripts/dev-db-up.sh

db-wipe:
	$(COMPOSE) down -v --remove-orphans
	./scripts/dev-db-up.sh

run:
	./scripts/dev-native-up.sh

restart:
	./scripts/dev-native-restart.sh

stop:
	./scripts/dev-native-down.sh

openapi:
	API_HOST=$(API_HOST) OUTPUT_FILE=$(OUTPUT_FILE) ./export-openapi-spec.sh

test:
	./scripts/run-tests.sh smoke

test-live:
	./scripts/run-tests.sh integration

# Deprecated aliases (hidden from help)
dev-db: db-up
dev: run
dev-refresh: restart
dev-stop: stop
test-smoke: test
test-integration: test-live
