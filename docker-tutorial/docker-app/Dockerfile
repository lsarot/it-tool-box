FROM node:12-alpine
##ambos WORKDIR funcionaron, pero quise usar el puerto 8080 (al hacer docker run -dp 8080:3000 ...) y fallaba, como que intentaba abrir otra imagen que ya estaba usando dicho puerto.
WORKDIR /usr/src/app
#WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

#Exposing port 3000 informs Docker which port the container is listening on at runtime!
#igual tendré que indicar al levantar el contenedor, esto es como para abrir el puerto y escucharlo
EXPOSE 3000

CMD ["npm", "start"]