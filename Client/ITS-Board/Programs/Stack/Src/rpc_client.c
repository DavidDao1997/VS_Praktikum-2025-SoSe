#include "rpc_client.h"
#include "err.h"
#include "marshall.h"
#include <stdint.h>
#include <stdio.h>
#include <string.h>


#define MYPORT 0xAFFE





// Clientserver festlegen 
// static ip_addr_t rpc_client_ip;
// Zielserver festlegen 
static ip_addr_t rpc_target_server_ip;

static uint16_t rpc_target_server_port = 45054;

static uint32_t last_heartbeat = 0;
static uint32_t last_timestamp = 0;

char* socket = "172.16.1.55:54045";


void set_server_ip(uint8_t a, uint8_t b, uint8_t c, uint8_t d) {
    IP4_ADDR(&rpc_target_server_ip, a, b, c, d);
}



// RPC-Client-Port (dynamisch)
static struct udp_pcb* udp_client_pcb = NULL;
static uint16_t rpc_id_counter = 1;

void receive_resolution(uint8_t a, uint8_t b, uint8_t c, uint8_t d, uint32_t port) {
    set_server_ip( a,  b,  c,  d);
    rpc_target_server_port = port;
}

static void udp_server_recv(void *arg, struct udp_pcb *pcb, struct pbuf *p,
                            const ip_addr_t *addr, u16_t port) {
    LWIP_UNUSED_ARG(arg);
    LWIP_UNUSED_ARG(pcb);

    if (p != NULL) {
        /* Empfangene Daten auf dem LCD ausgeben */
        char buffer[128];
        memcpy(buffer, p->payload, p->len > 127 ? 127 : p->len);
        buffer[p->len > 127 ? 127 : p->len] = '\0';

        char function[64];
        char params[RPC_MAX_PARAMS][64];   
        unmarshall(buffer, function, params);

        if (strcmp(function, "receiveResolution") == 0) {
            char* full = params[0];  // z.B. "172.16.1.1:123456"
    
            int ip1, ip2, ip3, ip4, port;
            if (sscanf(full, "%d.%d.%d.%d:%d", &ip1, &ip2, &ip3, &ip4, &port) == 5) {
                receive_resolution(ip1, ip2, ip3, ip4, port);
            }
        } else if (strcmp(function, "setTimestamp") == 0) {
            char* service = params[0]; 
            int time;

            if (sscanf(params[1], "%d", &time) == 1){
                // TODO put into timestamp table of service table
            }

        }

        /* Puffer freigeben */
        pbuf_free(p);
    }
}

// Initialisierung des Client-PCB
int rpc_init(void) {
    int err = ERR_OK;
    udp_client_pcb = udp_new();
    
    if (udp_client_pcb != NULL) {
        err = udp_bind(udp_client_pcb, IP_ADDR_ANY, 54045);

        if (ERR_OK != err){
            lcdPrintlnS("UDP-Client konnte nicht gestartet werden");
            memp_free(MEMP_UDP_PCB, udp_client_pcb);
            return err;
        } else {
            udp_recv(udp_client_pcb, udp_server_recv, NULL);
        }
    }
    return err;
}



// Hilfsfunktion zum Senden von RPC-Requests
static void rpc_send(const char* payload) {
    struct pbuf* p = pbuf_alloc(PBUF_TRANSPORT, strlen(payload), PBUF_RAM);
    if (!p){
        lcdPrintlnS("UDP-Client konnte nicht gestartet werden");
        return;
    } 
    
    memcpy(p->payload, payload, strlen(payload));
    udp_sendto(udp_client_pcb, p, &rpc_target_server_ip, rpc_target_server_port);
    pbuf_free(p);
}


void resolve_dns(char* servicename, char* functionname) {
    set_server_ip(172, 16, 1, 87); 
    rpc_target_server_port = 9000; 


    const char* params[] = { servicename, functionname, socket }; // myIP und Port
    char payload[256];
    // senden resolve (servicename, funktionsname, Myip:Myport)
    if (ERR_OK == marshall("resolve", params, 3, payload)) {
        // Sende den Payload an den Server
        rpc_send(payload);
    } 

    while (rpc_target_server_port == 9000){
        check_input();
    }

}


// Kompakte JSON-Kodierung der Funktionsaufrufe
void rpc_invoke(const char* func, const char* paramTypes[], const char* param[],
                const int numOfParam){
    char payload[256];


    marshall(func, param, numOfParam, payload);
    
    if (strcmp(func, "select") == 0) {
        resolve_dns("stateService", "select"); 

        //set_server_ip(172, 16, 1, 87); 
        //rpc_target_server_port = 63721; 

    } else if (strcmp(func, "move") == 0) {
        resolve_dns("moveAdapter", "move"); 

        //set_server_ip(172, 16, 1, 87); 
        //rpc_target_server_port = 63721; 

    }
   
    rpc_send(payload);
}




void rpc_send_heartbeat(uint32_t now) {
    if (now - last_heartbeat > 100) {
        err_t err = ERR_OK;//dns_gethostbyname(watchdog_hostname, &rpc_server_ip, dns_found_cb, NULL);
        if (err == ERR_OK) {
        
            last_heartbeat = now;
        
            // IP war schon gecached, direkt senden
            const char* params[] = { "IO" };
            char payload[256];

            marshall("heartbeat", params, 1, payload);


            set_server_ip(172, 16, 1, 87);
            rpc_target_server_port =0xAFFA; 
            rpc_send(payload);
        }
    }
}

void rpc_send_timestamp(uint32_t now) {
    if (now - last_heartbeat > 250) {
        err_t err = ERR_OK;
        if (err == ERR_OK) {
        
            last_heartbeat = now;
        
            char act_timestamp[16];
            sprintf(act_timestamp, "%d", now);
            // IP war schon gecached, direkt senden
            const char* params[] = { "IO", act_timestamp};
            char payload[256];

            marshall("setTimestamp", params, 2, payload);

            
            set_server_ip(172, 16, 1, 255);
            // UDP braucht einen Port
            rpc_target_server_port =0xAFFA; 
            // rpc_send(payload);
        }
    }
}

