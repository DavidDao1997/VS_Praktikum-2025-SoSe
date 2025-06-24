#include "rpc_client.h" 
#include <stdint.h>
#include <string.h>
#include "lwip/udp.h"

#include "app_stub.h"


// Hilfsfunktion: Enum zu String
const char* dir_to_str(Direction dir) {
    switch (dir) {
        case DIR_UP: return "U";
        case DIR_DOWN:  return "D";
        case DIR_LEFT: return "L";
        case DIR_RIGHT:  return "R";
        case DIR_FORWARD: return "F";
        case DIR_BACKWARD:  return "B";
        case DIR_OPEN: return "O";
        case DIR_CLOSE:  return "C";
        default:        return "?";
    }
}

const char* sdir_to_str(SDirection dir) {
    switch (dir) {
        case SELECT_UP: return "1";
        case SELECT_DOWN: return "2";
        default:     return "?";
    }
}



// Exponierte Funktionen zur App
void move(Direction dir) {
    rpc_invoke("move", "d", dir_to_str(dir));
}

void register_node(const char* name) {
    rpc_invoke("register", "n", name);
}

void select(SDirection dir) {
    rpc_invoke("select", "d", sdir_to_str(dir));
}