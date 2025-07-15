#include "caching_proxy.h"
#include <stdint.h>
#include <string.h>
#include <stdio.h>
#include "stm32f4xx_hal.h"

#define CACHE_SIZE 2
#define LIFETIME 300000


#define DNS_PORT 9000
#define MAX_RESOLVE_TIME 500

static struct udp_pcb* udp_send_pcb = NULL;
char* socket = "172.16.1.55:54045";

ip_addr_t rpc_target_server_ip;

uint16_t rpc_target_server_port = 0;

typedef struct {
    char servicename[32];
    char functionname[32];
    char socket[32]; // z.B. "172.16.1.1:12345"
    uint32_t time;
    int valid;
} cache_entry_t;

static cache_entry_t cache[CACHE_SIZE];


static void rpc_send(const char* payload) {
    struct pbuf* p = pbuf_alloc(PBUF_TRANSPORT, strlen(payload), PBUF_RAM);
    if (!p){
        return;
    } 
    
    memcpy(p->payload, payload, strlen(payload));
    udp_sendto(udp_send_pcb, p, &rpc_target_server_ip, rpc_target_server_port);
    pbuf_free(p);
}

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

void set_server_ip(uint8_t a, uint8_t b, uint8_t c, uint8_t d) {
    IP4_ADDR(&rpc_target_server_ip, a, b, c, d);
}

const char* resolve_proxy_dns(const char* servicename,const char* functionname) {
    const char* cached = cache_lookup(servicename, functionname);
    if (cached) {
        return cached;
    }
    return NULL;
}

void receive_resolution(uint8_t a, uint8_t b, uint8_t c, uint8_t d, uint32_t port) {
    set_server_ip( a,  b,  c,  d);
    rpc_target_server_port = port;
}


void resolve_dns(const char* servicename, const char* functionname) {
    set_server_ip(172, 16, 1, 87); 
    rpc_target_server_port = 9000; 

    if (strcmp(servicename, "dns") == 0){
        return;
    }
    // First search in Cache
    const char * full = resolve_proxy_dns(servicename, functionname);

    if (NULL == full){
        // Not in Cache
        const char* params[] = { servicename, functionname, socket }; // myIP und Port
        char payload[PAYLOAD_FIXED_SIZE];
        uint32_t timestamp = 0;
        // senden resolve (servicename, funktionsname, Myip:Myport)
        if (ERR_OK == marshall("resolve", params, 3, payload, timestamp)) {
            // Sende den Payload an den Server
            rpc_send(payload);
        } 

        uint32_t start = HAL_GetTick();
        while ((rpc_target_server_port == DNS_PORT) && (HAL_GetTick() - start <= MAX_RESOLVE_TIME)){ //Add time max 500ms
            check_input();
        }
        if (rpc_target_server_port == DNS_PORT){
           receive_resolution(0, 0, 0, 0, 0);
        }
    } else {
        // Is in cache
        int ip1, ip2, ip3, ip4, port;
        if (sscanf(full, "%d.%d.%d.%d:%d", &ip1, &ip2, &ip3, &ip4, &port) == 5) {
                receive_resolution(ip1, ip2, ip3, ip4, port);
        }
    }
}

void proxy_send(const char * servicename,const  char* function, const char* param[],
            const int numOfParam, uint32_t timestamp ){
    char payload[PAYLOAD_FIXED_SIZE];

    resolve_dns(servicename, function);
    marshall(function, param, numOfParam, payload, timestamp);
    rpc_send(payload);

}


int rpc_proxy_init(void) {
    int err = ERR_OK;
    udp_send_pcb = udp_new();
    return err;
}