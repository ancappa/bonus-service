FROM java:8-alpine
RUN apk —update add tini
EXPOSE 8080
ENTRYPOINT ["tini", "--"]
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar", "-Xms64m"]