###SIN LAYERED JAR DE SPRINGBOOT 2.3.0M1
		#FROM openjdk:8-jdk-alpine
		#EXPOSE 8080
		#ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar
		#ADD ${JAR_FILE} app.jar
		#ENTRYPOINT ["java","-jar","/app.jar"]


###CON LAYERED JAR FEATURE

FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

	#estamos creando una image a partir de un jre v11, creamos un application.jar a partir del .jar original (fat jar que está usando layers) 
	#y ejecutamos extract para copiar al workdir (application) las capas (carpetas de la nueva estructura de nuestro fatjar).

FROM adoptopenjdk:11-jre-hotspot
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/resources/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

	#luego creamos una image a partir de un jre v11, copiamos de la otra capa todos los directorios a la ruta raíz
	#y establecemos un entrypoint... el resultado es el mismo, no podemos eliminar ninguna de estas capas, pero podemos utilizar la cache feature dado que sólo modificaremos la capa representada por application/application/