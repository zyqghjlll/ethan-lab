# Local service storage

Docker Compose stores durable service data in the project's `data/` directory:

| Folder | Contents |
| --- | --- |
| `data/grafana` | Grafana database (users, preferences, dashboards, data sources) and plugins |
| `data/postgres` | PostgreSQL 15 database cluster |
| `data/kafka` | Kafka messages and broker metadata |
| `data/zookeeper/data`, `data/zookeeper/log` | ZooKeeper snapshots and transaction logs |
| `data/redis` | Redis AOF and snapshots |
| `data/prometheus` | Prometheus time-series database, subject to configured retention |

These are host bind mounts. Restarting or recreating containers, including Compose down/up, keeps the files. `data/` and `backups/` are excluded from Git because they contain database files and potentially credentials. Run `scripts/prepare-local-data.sh` once to create the directories and record your host UID in the ignored `.env` file. Services run as that UID with group 0, which permits Confluent configuration writes and avoids ownership changes unsupported by Colima SSHFS. Do not run two stacks against the same data folders.

## Existing installations: migrate before starting the updated Compose file

If your original `dev-*` containers still exist, run:

```bash
./scripts/migrate-local-data.sh
./scripts/prepare-local-data.sh
docker-compose up -d
```

The script stops the stack, archives the current data under `backups/storage-migration-<timestamp>/`, and copies it to `data/`. It refuses to overwrite a nonempty destination. It leaves containers stopped if anything fails; inspect the error before proceeding. Do not recreate the original containers until all copies succeed. This migrates currently available data, not dashboards already lost from deleted containers.

For a fresh installation with no data to migrate, run `./scripts/prepare-local-data.sh` followed by `docker-compose up -d`.

## Backups and restoration

Persistence is not a backup: deleting the project folder or disk failure still destroys these files. Keep copies outside this machine. For a consistent filesystem backup, stop the services before archiving `data/`, then start them again. Store Kafka and ZooKeeper together. PostgreSQL's `init.sql` runs only when its data directory is empty.

To restore a migration archive, stop the services, preserve the current folder separately, and extract the corresponding archive into an empty service data directory. Start with compatible image versions; do not blindly downgrade an existing database. Extract as your host user so restored files belong to the UID in `.env`. When moving to another machine, update `LOCAL_UID` to that machine’s `id -u` and ensure the copied files belong to that user.

The stack currently includes floating image tags. Back up before pulling newer images: persistent files do not make database format upgrades reversible.
