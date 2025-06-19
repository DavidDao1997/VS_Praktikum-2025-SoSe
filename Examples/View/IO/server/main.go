// Package main implements a server for Greeter service.
package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"net"

	pb "hello-grpc-go/gen/move"

	"google.golang.org/grpc"
)

var (
	port = flag.Int("port", 50051, "The server port")
)

// server is used to implement helloworld.GreeterServer.
type server struct {
	pb.UnimplementedMoverServer
}

// SayHello implements helloworld.GreeterServer
func (s *server) Move(_ context.Context, in *pb.MoveRequest) (*pb.MoveResponse, error) {
	log.Printf("Received: %v", in.GetDirection())
	return &pb.MoveResponse{Result: "Movement " + in.GetDirection().String() + " successfull"}, nil
}

func main() {
	flag.Parse()
	lis, err := net.Listen("tcp", fmt.Sprintf(":%d", *port))
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	s := grpc.NewServer()
	pb.RegisterMoverServer(s, &server{})
	log.Printf("server listening at %v", lis.Addr())
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}
