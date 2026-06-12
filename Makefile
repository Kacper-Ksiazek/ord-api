.PHONY: help docker-restart openapi test

COMPOSE := docker compose
API_HOST ?= http://localhost:8080
OUTPUT_FILE ?= openapi.json

help:
	@echo "Available targets:"
	@echo "  docker-restart  Stop stack, wipe DB volume, remove app image, rebuild and start"
	@echo "  openapi         Export OpenAPI spec from a running API (default: openapi.json)"
	@echo "                  Requires the app to be up. Override: make openapi API_HOST=... OUTPUT_FILE=..."
	@echo "  test            Run full test suite (AllTestsSuite) with .env.test"

docker-restart:
	$(COMPOSE) down -v --rmi local --remove-orphans
	$(COMPOSE) up -d --build

openapi:
	API_HOST=$(API_HOST) OUTPUT_FILE=$(OUTPUT_FILE) ./export-openapi-spec.sh

test:
	bash -c 'set -a && source .env.test && set +a && \
	mvn -Dtest=com.ord.AllTestsSuite \
		-Dsurefire.parallel=none \
		-DforkCount=1 \
		-DreuseForks=false \
		test'
