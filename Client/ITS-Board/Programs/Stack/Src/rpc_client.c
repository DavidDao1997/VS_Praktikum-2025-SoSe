#include "rpc_client.h"
#include "err.h"
#include "marshall.h"
#include <stdint.h>
#include <string.h>





// Zielserver festlegen 
static ip_addr_t rpc_client_ip;


static ip_addr_t rpc_server_ip;
static uint16_t rpc_server_port = 0xAFFE;

static uint32_t last_heartbeat = 0;
const char* myIP = "192.198.33.99";
const uint32_t myPort = 0xAFFE;

void set_server_ip(uint8_t a, uint8_t b, uint8_t c, uint8_t d) {
    IP4_ADDR(&rpc_server_ip, a, b, c, d);
}

// RPC-Client-Port (dynamisch)
static struct udp_pcb* udp_client_pcb = NULL;
static uint16_t rpc_id_counter = 1;

static void udp_server_recv(void *arg, struct udp_pcb *pcb, struct pbuf *p,
                            const ip_addr_t *addr, u16_t port)
{
    LWIP_UNUSED_ARG(arg);
    LWIP_UNUSED_ARG(pcb);

    if (p != NULL) {
        /* Empfangene Daten auf dem LCD ausgeben */
        char buffer[128];
        memcpy(buffer, p->payload, p->len > 127 ? 127 : p->len);
        buffer[p->len > 127 ? 127 : p->len] = '\0';

        char function[64];
        char params[64];   
        int numOfParams = 0;
        unmarshall(buffer, function, &params);

        if (strcmp(function, "receive_resolution") == 0) {
            

        }

        /* Echo die Daten zurück */
        //udp_sendto(pcb, p, addr, port);

        /* Puffer freigeben */
        pbuf_free(p);
    }
}

// Initialisierung des Client-PCB
void rpc_client_init(void) {
    udp_client_pcb = udp_new();
    if (udp_client_pcb != NULL) {
        err_t err;

        /* Binde den PCB an den Port */
        err = udp_bind(udp_client_pcb, IP_ADDR_ANY, 0);

        if (err == ERR_OK) {
            /* Registriere Callback für empfangene Pakete */
            udp_recv(udp_client_pcb, udp_server_recv, NULL);

            // }
        } else {
            lcdPrintlnS("UDP-Server konnte nicht gestartet werden");
            memp_free(MEMP_UDP_PCB, udp_client_pcb);
        }
    }
}



// Hilfsfunktion zum Senden von RPC-Requests
static void rpc_send(const char* payload) {
    struct pbuf* p = pbuf_alloc(PBUF_TRANSPORT, strlen(payload), PBUF_RAM);
    if (!p) return;
    
    memcpy(p->payload, payload, strlen(payload));
    udp_sendto(udp_client_pcb, p, &rpc_server_ip, rpc_server_port);
    pbuf_free(p);
}





// Kompakte JSON-Kodierung der Funktionsaufrufe
void rpc_invoke(const char* func, const char* paramTypes[], const char* param[],
                const int numOfParam){
    char payload[256];


    marshall(func, param, numOfParam, payload);


                

    set_server_ip(192, 168, 33, 1); // TODO DNS
    rpc_server_port = 9000; // TODO DNS


    rpc_send(payload);
}



void receive_resolution();

void resolve_dns(char* servicename, char* functionname) {
    set_server_ip(192, 168, 33, 1); // TODO Watchdog 
    rpc_server_port = 9000; // TODO Watchdog

    const char* params[] = { servicename, functionname, myIP, "0xAFFE" }; // myIP und Port
    char payload[256];
    // senden resolve (servicename, funktionsname, Myip, Myport)
    marshall("resolve", params, 4, payload);

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


            set_server_ip(192, 168, 33, 1); // TODO Watchdog 
            rpc_server_port =0xAFFA; // TODO Watchdog
            rpc_send(payload);
        }
    }
}

