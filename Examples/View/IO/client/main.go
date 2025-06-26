// Package main implements a client for Greeter service.
package main

import (
	"context"
	"flag"
	"log"

	"time"

	"hello-grpc-go/gen/moveAdapter"
	"hello-grpc-go/gen/stateService"

	"github.com/gdamore/tcell/v2"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

const (
	defaultName = "world"
)

var (
	addr1 = flag.String("addr1", "localhost:50051", "The first port to connect to")
	addr2 = flag.String("addr2", "localhost:50052", "The second port to connect to")
	name  = flag.String("name", defaultName, "Name to greet")
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

type KeyButton struct {
	X, Y  int
	Sym   rune
	Label string
}

var keyPositions = map[string]KeyButton{
	// Movement
	"up":    {30, 2, 'w', "Up"},
	"left":  {24, 4, 'a', "Left"},
	"down":  {30, 4, 's', "Down"},
	"right": {36, 4, 'd', "Right"},

	// Forward / Backward
	"backward": {24, 6, 'q', "Backward"},
	"forward":  {36, 6, 'e', "Forward"},

	// Open / Close
	"open":  {24, 8, '1', "Open"},
	"close": {36, 8, '2', "Close"},

	// Select up/down
	"select_up":   {24, 10, '3', "Select↑"},
	"select_down": {36, 10, '4', "Select↓"},
}

func drawArrows(s tcell.Screen, highlight string) {
	s.Clear()

	for name, btn := range keyPositions {
		style := tcell.StyleDefault.Foreground(tcell.ColorWhite).Background(tcell.ColorBlack)
		if name == highlight {
			style = tcell.StyleDefault.Foreground(tcell.ColorBlack).Background(tcell.ColorGreen)
		}
		// Draw the key character in a box-like format
		s.SetContent(btn.X, btn.Y, '[', nil, style)
		s.SetContent(btn.X+1, btn.Y, btn.Sym, nil, style)
		s.SetContent(btn.X+2, btn.Y, ']', nil, style)

		labelX := btn.X + 2 - len(btn.Label)/2
		for i, r := range btn.Label {
			s.SetContent(labelX+i, btn.Y+1, r, nil, style)
		}
	}
	s.Show()
}

func main() {
	flag.Parse()
	
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
	conn, err := grpc.NewClient(*addr1, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}
	defer conn.Close()
	c := moveAdapter.NewMoveAdapterClient(conn)

	conn2, err := grpc.NewClient(*addr2, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}
	defer conn2.Close()
	c2 := stateService.NewStateServiceClient(conn2)

	for {
		select {
		case ev := <-eventChan:
			switch tev := ev.(type) {
			case *tcell.EventKey:
				ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
				defer cancel()
				var err error
				switch tev.Key() {
				case tcell.KeyEscape, tcell.KeyCtrlC:
					return
				case tcell.KeyRune:
					switch tev.Rune() {
					case 'w':
						highlight = "up"
						_, err = c.Move(ctx, &moveAdapter.MoveRequest{Direction: moveAdapter.RobotDirection_UP})
					case 's':
						highlight = "down"
						_, err = c.Move(ctx, &moveAdapter.MoveRequest{Direction: moveAdapter.RobotDirection_DOWN})
					case 'a':
						highlight = "left"
						_, err = c.Move(ctx, &moveAdapter.MoveRequest{Direction: moveAdapter.RobotDirection_LEFT})
					case 'd':
						highlight = "right"
						_, err = c.Move(ctx, &moveAdapter.MoveRequest{Direction: moveAdapter.RobotDirection_RIGHT})
					case 'q':
						highlight = "backward"
						_, err = c.Move(ctx, &moveAdapter.MoveRequest{Direction: moveAdapter.RobotDirection_BACKWARD})
					case 'e':
						highlight = "forward"
						_, err = c.Move(ctx, &moveAdapter.MoveRequest{Direction: moveAdapter.RobotDirection_FORWARD})
					case '1':
						highlight = "open"
						_, err = c.Move(ctx, &moveAdapter.MoveRequest{Direction: moveAdapter.RobotDirection_OPEN})
					case '2':
						highlight = "close"
						_, err = c.Move(ctx, &moveAdapter.MoveRequest{Direction: moveAdapter.RobotDirection_CLOSE})
					case '3':
						highlight = "select_up"
						_, err = c2.Select(ctx, &stateService.SelectRequest{SelectDirection: stateService.SelectDirection_UP})
					case '4':
						highlight = "select_down"
						_, err = c2.Select(ctx, &stateService.SelectRequest{SelectDirection: stateService.SelectDirection_DOWN})
					default:
						continue
					}
				default:
					continue
				}
				if err != nil {
					printError(s, err.Error())
					// Wait for user to press a key before exiting
					s.PollEvent()
					return

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

func printError(s tcell.Screen, msg string) {
	s.Clear() // Clear the screen

	// Set style (red text on default background)
	style := tcell.StyleDefault.Foreground(tcell.ColorRed).Background(tcell.ColorReset)

	// Print the error message at (0,0)
	for i, r := range msg {
		s.SetContent(i, 0, r, nil, style)
	}

	s.Show() // Refresh the screen to display changes
}
