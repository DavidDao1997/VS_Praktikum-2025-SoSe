#include "rpc_client.h" 
#include <stdint.h>
#include <string.h>
#include "lwip/udp.h"

#include "app_stub.h"


// Hilfsfunktion: Enum zu String
const char* dir_to_str(Direction dir) {
    switch (dir) {
        case DIR_UP: return "0";
        case DIR_DOWN:  return "1";
        case DIR_LEFT: return "2";
        case DIR_RIGHT:  return "3";
        case DIR_FORWARD: return "4";
        case DIR_BACKWARD:  return "5";
        case DIR_OPEN: return "6";
        case DIR_CLOSE:  return "7";
        default:        return "99";
    }
}

const char* sdir_to_str(SDirection dir) {
    switch (dir) {
        case SELECT_UP: return "0";
        case SELECT_DOWN: return "1";
        default:     return "99";
    }
}



// Exponierte Funktionen zur App
void move(Direction dir) {
    const char* paramTypes[] = { "int" };
    const char* param[] = {dir_to_str(dir)};
    int numOfParam = 1;

    rpc_invoke("move", paramTypes, param, numOfParam);
}

void register_node( char* name, char* functionName) {
    const char* paramTypes[] = { "String", "String" };
    const char* param[] = {name, functionName};
    int numOfParam = 2;
    rpc_invoke("register_node", paramTypes, param, numOfParam);
}

void select(SDirection dir) {
    const char* paramTypes[] = { "int" };

    const char* param[] = {sdir_to_str(dir)};
    int numOfParam = 1;
    rpc_invoke("select", paramTypes, param, numOfParam);
}