#!/usr/bin/env bash
set -a

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ -f "$SCRIPT_DIR/.env" ]; then
  # Load local backend config, including API-Football credentials.
  . "$SCRIPT_DIR/.env"
fi

set +a

cd "$SCRIPT_DIR"
exec mvn spring-boot:run
