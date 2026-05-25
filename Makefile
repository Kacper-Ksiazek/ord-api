.PHONY: help docker-restart

COMPOSE := docker compose

help:
	@echo "Available targets:"
	@echo "  docker-restart  Stop stack, wipe DB volume, remove app image, rebuild and start"

docker-restart:
	$(COMPOSE) down -v --rmi local --remove-orphans
	$(COMPOSE) up -d --build
