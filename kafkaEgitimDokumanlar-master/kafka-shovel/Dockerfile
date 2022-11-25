FROM golang:1.16.13-stretch AS builder
WORKDIR /kafka-shovel
ENV GO111MODULE=on
ENV CGO_ENABLED=0

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN go build

EXPOSE 8080

FROM alpine:3.11.0

# config
ENV KAFKAVERSION=2.1.2
ENV RETRYCOUNT=5
ENV ERRORSUFFIX="error"
ENV RETRYSUFFIX="retry"
ENV GROUPNAME="egm-kafka-shovel"
ENV TOPICS="ssmdb.yazilim.user-created.0.error:true, ssmdb.twitter-data.0.error:true"
ENV BROKERS=kafka:29092
ENV DURATION=1
# config end

RUN apk add --update --no-cache ca-certificates && rm -rf /var/cache/apk/*
COPY --from=builder /kafka-shovel/kafka-shovel /

ENTRYPOINT ./kafka-shovel
