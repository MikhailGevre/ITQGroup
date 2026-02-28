#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

TARGET_REL="src/main/resources/application.properties"
TARGET="$PROJECT_ROOT/$TARGET_REL"
GITIGNORE="$PROJECT_ROOT/.gitignore"

if [ -f "$TARGET" ]; then
  echo "Файл уже существует: $TARGET_REL"
else
  echo "Создаю $TARGET_REL ..."
  mkdir -p "$(dirname "$TARGET")"

  cat <<EOF > "$TARGET"
POSTGRES_DB=
DB_NAME=
POSTGRES_HOST=
POSTGRES_PORT=
POSTGRES_USER=
POSTGRES_PASSWORD=
EOF

  echo "Файл создан."
fi

if [ -f "$GITIGNORE" ]; then
  if grep -qxF "$TARGET_REL" "$GITIGNORE"; then
    echo ".gitignore уже содержит запись."
  else
    echo "$TARGET_REL" >> "$GITIGNORE"
    echo "Добавлено в .gitignore."
  fi
else
  echo "$TARGET_REL" > "$GITIGNORE"
  echo "Создан .gitignore и добавлена запись."
fi

echo "Готово."