#!/usr/bin/env bash
set -e

ROOT_DIR="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"

if [ -f "$ROOT_DIR/.env" ]; then
  echo ".env уже существует в $ROOT_DIR"
  exit 0
fi

cat > "$ROOT_DIR/.env" <<EOF
POSTGRES_USER=
POSTGRES_PASSWORD=
EOF
echo ".env" >> .gitignore

echo ".env успешно создано $ROOT_DIR"