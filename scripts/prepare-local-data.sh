#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
# Use the host owner for bind mounts; group 0 allows Confluent config writes.
if ! grep -q '^LOCAL_UID=' .env 2>/dev/null; then
  printf '\nLOCAL_UID=%s\n' "$(id -u)" >> .env
fi
mkdir -p data/{grafana,prometheus,postgres,redis,kafka,zookeeper/data,zookeeper/log}
