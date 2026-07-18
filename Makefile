.PHONY: help docker-restart openapi test test-smoke test-integration

COMPOSE := docker compose
API_HOST ?= http://localhost:8080
OUTPUT_FILE ?= openapi.json
MAVEN_TEST_FLAGS := -Dsurefire.parallel=none -DforkCount=1 -DreuseForks=false

help:
	@echo "Available targets:"
	@echo "  docker-restart  Stop stack, wipe DB volume, remove app image, rebuild and start"
	@echo "  openapi         Export OpenAPI spec from a running API (default: openapi.json)"
	@echo "                  Requires the app to be up. Override: make openapi API_HOST=... OUTPUT_FILE=..."
	@echo "  test            Alias for test-smoke (default, no OpenAI API key required)"
	@echo "  test-smoke      Run full suite with AI stubs (no external OpenAI calls)"
	@echo "  test-integration Run full suite against real OpenAI API (requires OPEN_AI_KEY in .env.test)"

docker-restart:
	$(COMPOSE) down -v --rmi local --remove-orphans
	$(COMPOSE) up -d --build

openapi:
	API_HOST=$(API_HOST) OUTPUT_FILE=$(OUTPUT_FILE) ./export-openapi-spec.sh

test: test-smoke

test-smoke:
	bash -c 'set -a && [ -f .env.test ] && source .env.test; set +a && \
	mvn -Dtest=com.ord.AllTestsSuite \
		$(MAVEN_TEST_FLAGS) \
		test'

test-integration:
	bash -c 'set -a && source .env.test && set +a && \
	INTEGRATION_TESTS=true mvn -Dtest=com.ord.AllTestsSuite \
		$(MAVEN_TEST_FLAGS) \
		test'
