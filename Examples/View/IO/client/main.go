// Package main implements a client for Greeter service.
package main

import (
	"context"
	"flag"
	"log"

	"time"

	pb "hello-grpc-go/gen/move"

	"github.com/gdamore/tcell/v2"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

const (
	defaultName = "world"
)

var (
	addr = flag.String("addr", "localhost:50051", "the address to connect to")
	name = flag.String("name", defaultName, "Name to greet")
)

// func main() {
// 	flag.Parse()
// 	// Set up a connection to the server.
// 	conn, err := grpc.NewClient(*addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
// 	if err != nil {
// 		log.Fatalf("did not connect: %v", err)
// 	}
// 	defer conn.Close()
// 	c := pb.NewGreeterClient(conn)

// 	// Contact the server and print out its response.
// 	ctx, cancel := context.WithTimeout(context.Background(), time.Second)
// 	defer cancel()
// 	r, err := c.SayHello(ctx, &pb.HelloRequest{Name: *name})
// 	if err != nil {
// 		log.Fatalf("could not greet: %v", err)
// 	}
// 	log.Printf("Greeting: %s", r.GetMessage())
// }

var arrowPositions = map[string]struct {
	X, Y int
	Sym  rune
}{
	"up":    {10, 2, '↑'},
	"down":  {10, 6, '↓'},
	"left":  {6, 4, '←'},
	"right": {14, 4, '→'},
}

func drawArrows(s tcell.Screen, highlight string) {
	s.Clear()

	for name, pos := range arrowPositions {
		style := tcell.StyleDefault.Foreground(tcell.ColorWhite).Background(tcell.ColorBlack)
		if name == highlight {
			style = tcell.StyleDefault.Foreground(tcell.ColorBlack).Background(tcell.ColorGreen)
		}
		s.SetContent(pos.X, pos.Y, pos.Sym, nil, style)
	}

	s.Show()
}

func main() {
	s, err := tcell.NewScreen()
	if err != nil {
		panic(err)
	}
	if err := s.Init(); err != nil {
		panic(err)
	}
	defer s.Fini()

	s.Clear()
	drawArrows(s, "")

	eventChan := make(chan tcell.Event, 10)

	// Goroutine to poll input and send to channel
	go func() {
		for {
			ev := s.PollEvent()
			eventChan <- ev
		}
	}()

	highlight := ""
	highlightUntil := time.Time{}

	ticker := time.NewTicker(50 * time.Millisecond)
	defer ticker.Stop()

	// setup grpc
	conn, err := grpc.NewClient(*addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}
	defer conn.Close()
	c := pb.NewMoverClient(conn)

	for {
		select {
		case ev := <-eventChan:
			switch tev := ev.(type) {
			case *tcell.EventKey:
				ctx, cancel := context.WithTimeout(context.Background(), time.Second)
				defer cancel()
				var err error
				switch tev.Key() {
				case tcell.KeyEscape, tcell.KeyCtrlC:
					return
				case tcell.KeyUp:
					highlight = "up"
					_, err = c.Move(ctx, &pb.MoveRequest{Direction: pb.Direction_JOINT1_CW})
				case tcell.KeyDown:
					highlight = "down"
					_, err = c.Move(ctx, &pb.MoveRequest{Direction: pb.Direction_JOINT1_CCW})
				case tcell.KeyLeft:
					highlight = "left"
					_, err = c.Move(ctx, &pb.MoveRequest{Direction: pb.Direction_ROTATION_CW})
				case tcell.KeyRight:
					highlight = "right"
					_, err = c.Move(ctx, &pb.MoveRequest{Direction: pb.Direction_ROTATION_CCW})
				default:
					continue
				}
				if err != nil {
					log.Fatalf("Fatal Error: %s", err)
				}

				highlightUntil = time.Now().Add(150 * time.Millisecond)
				drawArrows(s, highlight)

			case *tcell.EventResize:
				s.Sync()
			}

		case <-ticker.C:
			// Clear highlight if time has passed
			if !highlightUntil.IsZero() && time.Now().After(highlightUntil) {
				highlight = ""
				drawArrows(s, "")
				highlightUntil = time.Time{}
			}
		}
	}
}
