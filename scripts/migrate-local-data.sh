#!/usr/bin/env bash
# Run once, BEFORE recreating the original containers with the new bind mounts.
set -euo pipefail
cd "$(dirname "$0")/.."
containers=(dev-grafana dev-prometheus dev-postgres dev-redis dev-kafka dev-zookeeper dev-zookeeper)
paths=(/var/lib/grafana /prometheus /var/lib/postgresql/data /data /var/lib/kafka/data /var/lib/zookeeper/data /var/lib/zookeeper/log)
dirs=(grafana prometheus postgres redis kafka zookeeper/data zookeeper/log)
for i in "${!containers[@]}"; do
  docker inspect "${containers[$i]}" >/dev/null
  if [ -d "data/${dirs[$i]}" ] && [ -n "$(ls -A "data/${dirs[$i]}")" ]; then
    echo "Refusing to overwrite data/${dirs[$i]}. Migration must start with empty destinations." >&2
    exit 1
  fi
done
backup="backups/storage-migration-$(date +%Y%m%d-%H%M%S)"
umask 077
mkdir -p "$backup"
docker inspect dev-grafana dev-prometheus dev-postgres dev-redis dev-kafka dev-zookeeper > "$backup/containers.json"
# Stop writers before copying their database files. Do not remove any containers.
docker stop --timeout 60 dev-kafka-ui dev-grafana dev-prometheus dev-kafka dev-zookeeper dev-postgres dev-redis
for i in "${!containers[@]}"; do
  archive="${dirs[$i]//\//-}.tar"
  docker cp "${containers[$i]}:${paths[$i]}/." - > "$backup/$archive"
  mkdir -p "data/${dirs[$i]}"
  docker cp "${containers[$i]}:${paths[$i]}/." "data/${dirs[$i]}/"
done
echo "Migration copied successfully. Backup: $backup"
echo "Prepare permissions with: ./scripts/prepare-local-data.sh"
echo "Start with: docker-compose up -d"
