#Build the container image using (using this build config file)
#docker build -t getting-started .          -t flag tags our image
#then run with: docker run -dp 3000:3000 getting-started

###start from the node:12-alpine image
#FROM node:12-alpine
#WORKDIR /app
###After the image was downloaded, we copied in our application
#COPY . .
###and used yarn to install our application's dependencies
#RUN yarn install --production
###default command to run when starting a container from this image.
#CMD ["node", "/app/src/index.js"]


############################
### HACEMOS UNA MODIFICACIÓN PARA USAR LAYER CACHING, y que los build posteriores sean más rápidos

###an important lesson to learn to help decrease build times for your container images.
	#Once a layer changes, all downstream layers have to be recreated as well

#before, when we made a change to the image, the yarn dependencies had to be reinstalled.
#To fix this, we need to restructure our Dockerfile to help support the caching of some content (the dependencies).
#For Node-based applications, those dependencies are defined in the package.json file. 
#So, what if we copied only that file in first, install the dependencies, and then copy in everything else? 
#Then, we only recreate the yarn dependencies if there was a change to the package.json. 

FROM node:12-alpine
WORKDIR /app

#changes are here! Added this line and changed order of others
#la razón para que funcione es la sig: antes copiabamos el proyecto entero primero y luego hacíamos yarn install. 
#Si había cambio respecto al contenido anterior de la imagen, se hacía todo el proceso desde el inicio, incluso instalando nuevamente las yarn dependencies, 
#aunque el cambio fuera en el source code. Esta vez, copiamos estos ficheros primero, luego yarn install y luego copiamos el resto del contenido (ie. source code).
#si hay cambios en package.json, yarn install detecta un cambio y reinstala todas las dependencies
#los cambios en el source code no harán que se instalen las dependencias nuevamente, pq el sourcecode se copia luego
COPY package.json yarn.lock ./
RUN yarn install --production
COPY . .

CMD ["node", "/app/src/index.js"]



