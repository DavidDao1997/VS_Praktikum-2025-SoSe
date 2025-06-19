package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"net"

	"hello-grpc-go/gen/moveAdapter"
	"hello-grpc-go/gen/stateService"

	"google.golang.org/grpc"
	// import your moveAdapter and server implementation
)

// server is used to implement helloworld.GreeterServer.
type moveAdapterServer struct {
	moveAdapter.UnimplementedMoveAdapterServer
}

type stateServiceServer struct {
	stateService.UnimplementedStateServiceServer
}

// SayHello implements helloworld.GreeterServer
func (s *moveAdapterServer) Move(_ context.Context, in *moveAdapter.MoveRequest) (*moveAdapter.MoveResponse, error) {
	log.Printf("(moveAdapterServer) Received: %v", in.GetDirection())
	return &moveAdapter.MoveResponse{Success: true}, nil
}

func (s *stateServiceServer) Select(_ context.Context, in *stateService.SelectRequest) (*stateService.Empty, error) {
	log.Printf("(stateServiceServer) Received: %v", in.GetSelectDirection())
	return &stateService.Empty{}, nil
}

var (
	port1 = flag.Int("port1", 50051, "The first port to listen on")
	port2 = flag.Int("port2", 50052, "The second port to listen on")
)

func main() {
	flag.Parse()

	go func() {
		s := grpc.NewServer()
		moveAdapter.RegisterMoveAdapterServer(s, &moveAdapterServer{})
		if err := startServer(*port1, s); err != nil {
			log.Fatalf("failed to start server on port %d: %v", *port1, err)
		}
	}()

	go func() {
		s := grpc.NewServer()
		stateService.RegisterStateServiceServer(s, &stateServiceServer{})
		if err := startServer(*port2, s); err != nil {
			log.Fatalf("failed to start server on port %d: %v", *port2, err)
		}
	}()

	// Block forever
	select {}
}

func startServer(port int, s *grpc.Server) error {
	lis, err := net.Listen("tcp", fmt.Sprintf(":%d", port))
	if err != nil {
		return fmt.Errorf("failed to listen: %w", err)
	}

	log.Printf("gRPC server listening at %v", lis.Addr())
	return s.Serve(lis)
}
