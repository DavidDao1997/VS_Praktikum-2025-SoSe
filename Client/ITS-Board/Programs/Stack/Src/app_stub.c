#include "rpc_client.h" 
#include <stdint.h>
#include <string.h>
#include "lwip/udp.h"

#include "app_stub.h"


// Hilfsfunktion: Enum zu String
const char* dir_to_str(Direction dir) {
    switch (dir) {
        case DIR_UP: return "DIR_UP";
        case DIR_DOWN:  return "DIR_DOWN";
        case DIR_LEFT: return "DIR_LEFT";
        case DIR_RIGHT:  return "DIR_RIGHT";
        case DIR_FORWARD: return "DIR_FORWARD";
        case DIR_BACKWARD:  return "DIR_BACKWARD";
        case DIR_OPEN: return "DIR_OPEN";
        case DIR_CLOSE:  return "DIR_CLOSE";
        default:        return "UNKNOWN";
    }
}

const char* sdir_to_str(SDirection dir) {
    switch (dir) {
        case SELECT_UP: return "SELECT_UP";
        case SELECT_DOWN: return "SELECT_DOWN";
        default:     return "UNKNOWN";
    }
}



// Exponierte Funktionen zur App
void move(Direction dir) {
    char* paramTypes[] = { "int" };
    char* param[] = {dir_to_str(dir)};
    int numOfParam = 1;

    rpc_invoke("move", &paramTypes, &param, numOfParam);
}

void register_node( char* name, char* functionName) {
    char* paramTypes[] = { "String", "String" };
    char* param[] = {name, functionName};
    int numOfParam = 2;
    rpc_invoke("register_node", &paramTypes, &param, numOfParam);
}

void select(SDirection dir) {
    char* paramTypes[] = { "int" };
    char* param[] = {dir_to_str(dir)};
    int numOfParam = 1;
    rpc_invoke("select", &paramTypes, &param, numOfParam);
}