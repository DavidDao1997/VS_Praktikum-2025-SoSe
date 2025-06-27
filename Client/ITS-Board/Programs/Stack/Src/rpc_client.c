#include "rpc_client.h"





// Zielserver festlegen 
static ip_addr_t rpc_server_ip;
static uint16_t rpc_server_port = 0xAFFE;

// void app_stub_set_server_ip(uint8_t a, uint8_t b, uint8_t c, uint8_t d) {
//     IP4_ADDR(&rpc_server_ip, a, b, c, d);
// }

// RPC-Client-Port (dynamisch)
static struct udp_pcb* udp_client_pcb = NULL;
static uint16_t rpc_id_counter = 1;


// Initialisierung des Client-PCB
void rpc_client_init(void) {
    udp_client_pcb = udp_new();
    if (!udp_client_pcb) {
        lcdPrintlnS("UDP Client Fehler");
        return;
    }
    udp_bind(udp_client_pcb, IP_ADDR_ANY, 0); // dynamischer Port
}

// Hilfsfunktion zum Senden von RPC-Requests
static void rpc_send(const char* payload) {
    struct pbuf* p = pbuf_alloc(PBUF_TRANSPORT, strlen(payload), PBUF_RAM);
    if (!p) return;

    memcpy(p->payload, payload, strlen(payload));
    udp_sendto(udp_client_pcb, p, rpc_server_ip, rpc_server_port);
    pbuf_free(p);
}

// Kompakte JSON-Kodierung der Funktionsaufrufe
void rpc_invoke(const char* func, const char* paramTypes, const char* param,
                const int numOfParam){
    char payload[256];
    

    marshall(&func, &paramTypes, &param, &numOfParam, &payload);

    rpc_send(payload);
}