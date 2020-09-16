FROM trufflesuite/ganache-cli:latest

RUN apk add --no-cache dos2unix
RUN npm install -g ganache-cli

COPY ./docker-entrypoint.sh /docker-entrypoint.sh
RUN dos2unix /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]
