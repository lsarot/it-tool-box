# CLONE THE TUTORIAL REPO

docker run --name repo alpine/git clone https://github.com/docker/getting-started.git
docker cp repo:/git/getting-started/ .


# BUILD THE IMAGE
# -t de tag, asignamos un tag a la imagen a construir, sino se crea uno aleatorio

cd getting-started
docker build -t docker101tutorial .


# RUN THE CONTAINER
# docker-tutorial es nombre del container, docker101tutorial es el nombre de la imagen construída anteriormente

docker run -d -p 80:80 --name docker-tutorial docker101tutorial


# SAVE AND SHARE YOUR IMAGE
# reasignamos con otro tag a la imagen docker101tutorial
# pusheamos la imagen al repo dockerhub

docker tag docker101tutorial docleo2020/docker101tutorial
docker push docleo2020/docker101tutorial
