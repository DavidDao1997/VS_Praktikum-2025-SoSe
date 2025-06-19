FROM mcr.microsoft.com/devcontainers/go:1-1.24-bookworm

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        protobuf-compiler \
        curl \
        unzip \
        git && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN go install google.golang.org/protobuf/cmd/protoc-gen-go@latest && \
    go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest

