#include "rpc_client.h" // enthält rpc_invoke(...)
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

// Zielserver festlegen (vllt global)
static ip_addr_t rpc_server_ip;
static uint16_t rpc_server_port = 0xAFFE;

void app_stub_set_server_ip(uint8_t a, uint8_t b, uint8_t c, uint8_t d) {
    IP4_ADDR(&rpc_server_ip, a, b, c, d);
}

// Exponierte Funktionen zur App
void move(Direction dir) {
    rpc_invoke("move", "d", dir_to_str(dir), &rpc_server_ip, rpc_server_port);
}

void register_node(const char* name) {
    rpc_invoke("register", "n", name, &rpc_server_ip, rpc_server_port);
}

void select(SDirection dir) {
    rpc_invoke("select", "d", sdir_to_str(dir), &rpc_server_ip, rpc_server_port);
}