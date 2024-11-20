How to run it with Docker Desktop:

mvn clean install

docker volume create datalake
docker volume create metadata
docker volume create datamart

docker build -f crawler/Dockerfile -t crawler-image .
docker run -d --name crawler-container -v datalake:/app/data/datalake -v metadata:/app/data/metadata crawler-image
docker stop crawler-container
docker start crawler-container

docker build -f indexer/Dockerfile -t expanded-indexer-image .
docker run -d --name expanded-indexer-container -v datalake:/app/data/datalake -v datamart:/app/data/datamart expanded-indexer-image

docker build -f indexer/Dockerfile --build-arg PROFILE=aggregatedIndexer -t aggregated-indexer-image .
docker run -d --name aggregated-indexer-container -v datalake:/data/datalake -v datamart:/data/datamart aggregated-indexer-image

docker build -f query-engine/Dockerfile -t query-engine-image .
docker run -it --name query-engine-container -v datalake:/app/data/datalake -v metadata:/app/data/metadata -v datamart:/app/data/datamart query-engine-image
