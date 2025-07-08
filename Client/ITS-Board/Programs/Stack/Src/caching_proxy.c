#include "caching_proxy.h"
#include <stdint.h>
#include <string.h>
#include <stdio.h>
#include "stm32f4xx_hal.h"

#define CACHE_SIZE 2
#define LIFETIME 300000

typedef struct {
    char servicename[32];
    char functionname[32];
    char socket[32]; // z.B. "172.16.1.1:12345"
    uint32_t time;
    int valid;
} cache_entry_t;

static cache_entry_t cache[CACHE_SIZE];

// Hilfsfunktion: Suche im Cache
static const char* cache_lookup(const char* servicename, const char* functionname) {
    for (int i = 0; i < CACHE_SIZE; ++i) {
        if (cache[i].valid &&
            strcmp(cache[i].servicename, servicename) == 0 &&
            strcmp(cache[i].functionname, functionname) == 0) {
            uint32_t now = HAL_GetTick();
            if (now - cache[i].time < LIFETIME){
                return cache[i].socket;
            } else {
                cache[i].valid = 0;
            }
        }
    }
    return NULL;
}

int cache_store(const char* servicename, const char* functionname, const char* socket, uint32_t time) {
    for (int i = 0; i < CACHE_SIZE; ++i) {
        if (!cache[i].valid ||
            (strcmp(cache[i].servicename, servicename) == 0 &&
             strcmp(cache[i].functionname, functionname) == 0)) {
            strncpy(cache[i].servicename, servicename, sizeof(cache[i].servicename)-1);
            strncpy(cache[i].functionname, functionname, sizeof(cache[i].functionname)-1);
            strncpy(cache[i].socket, socket, sizeof(cache[i].socket)-1);
            cache[i].time = time;
            cache[i].valid = 1;
            return 0;
        }
    }
    return -1;
}

const char* resolve_proxy_dns(char* servicename, char* functionname) {
    const char* cached = cache_lookup(servicename, functionname);
    if (cached) {
        return cached;
    }
    return NULL;
}
