#ifndef RPC_IDL_H
#define RPC_IDL_H

#include <stdint.h>
#include <string.h>
#include "stdlib.h"


#define RPC_MAX_PARAMS 4

//char function[64];
//char params[RPC_MAX_PARAMS][64];

// Funktionsdefinition
typedef struct {
    const char* name;
    const char* paramTypes[4]; // max 4 Parameter
    int numParams;
} RpcFunction;

// IDL-Tabelle
static const RpcFunction rpcFunctionTable[] = {
    { "move",          { "int" },                         1 },
    { "register_node", { "String", "String" },            2 },
    { "select",        { "int" },                         1 },
    {"heartbeat",      { "String" },                         1 },
    {"resolve",       { "String", "String", "String"}, 3 },
    { "receiveResolution", { "String" },             1 },
    {"setTimestamp",{"String", "int"}, 2},

    // TODO ADD ALL FUNCTIONS

};

static const int rpcFunctionCount = sizeof(rpcFunctionTable) / sizeof(RpcFunction);

// Hilfsfunktion zur Funktionserkennung
static inline const RpcFunction* find_rpc_function(const char* name) {
    for (int i = 0; i < rpcFunctionCount; ++i) {
        if (strcmp(rpcFunctionTable[i].name, name) == 0) {
            return &rpcFunctionTable[i];
        }
    }
    return NULL;
}

// Bitmap-Codierung: Setzt Bits für Rx-Einträge
static inline void encode_rlist_to_bitmap(const char* rlist[], int rcount, uint32_t out[8]) {
    for (int i = 0; i < 8; ++i) out[i] = 0;
    for (int i = 0; i < rcount; ++i) {
        if (rlist[i][0] == 'R') {
            int idx = atoi(rlist[i] + 1);
            if (idx >= 0 && idx < 256) {
                int slot = idx / 32;
                int bit = idx % 32;
                out[slot] |= (1U << bit);
            }
        }
    }
}

#endif // RPC_IDL_H