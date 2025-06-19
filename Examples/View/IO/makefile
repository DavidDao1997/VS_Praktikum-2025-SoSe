PROTO_DIR = proto

# Directory for generated Go code
OUT_DIR = gen

# Proto files
PROTO_FILES := $(wildcard $(PROTO_DIR)/*.proto)

# Command paths
PROTOC := protoc
PROTOC_GEN_GO := $(shell which protoc-gen-go)
PROTOC_GEN_GO_GRPC := $(shell which protoc-gen-go-grpc)

# Output options
GO_OUT := --go_out=$(OUT_DIR)
GO_GRPC_OUT := --go-grpc_out=$(OUT_DIR)

.PHONY: all clean generate build

all: generate

# Generate Go sources from .proto files
generate:
	@echo "Generating Go sources from .proto files..."
	@if [ -z "$(PROTOC_GEN_GO)" ] || [ -z "$(PROTOC_GEN_GO_GRPC)" ]; then \
		echo "Error: protoc-gen-go or protoc-gen-go-grpc not found in PATH."; \
		exit 1; \
	fi
	$(PROTOC) -I $(PROTO_DIR) $(GO_OUT) $(GO_GRPC_OUT) $(PROTO_FILES)

# Clean generated files
clean:
	@echo "Cleaning generated files..."
	@rm -rf $(OUT_DIR)

build:
	GOOS=darwin GOARCH=arm64 go build -o bin/client-mac-arm64 client/main.go;
	chmod +x bin/client-mac-arm64;
	GOOS=darwin GOARCH=arm64 go build -o bin/server-mac-arm64 server/main.go;
	chmod +x bin/server-mac-arm64;
	GOOS=windows GOARCH=amd64 go build -o bin/client-win-amd64.exe client/main.go;
	GOOS=windows GOARCH=amd64 go build -o bin/server-win-amd64.exe server/main.go;