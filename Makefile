all:
    docker-compose build

run:
    docker-compose up

jar:
    ./gradlew clean stage

purge:
	docker ps -a -q | xargs docker rm -f --volumes || true
	docker images -q | xargs docker rmi -f