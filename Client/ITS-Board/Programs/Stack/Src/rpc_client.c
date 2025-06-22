#include "rpc_client.h"


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
static void rpc_send(const char* payload, const ip_addr_t* dest_ip, uint16_t port) {
    struct pbuf* p = pbuf_alloc(PBUF_TRANSPORT, strlen(payload), PBUF_RAM);
    if (!p) return;

    memcpy(p->payload, payload, strlen(payload));
    udp_sendto(udp_client_pcb, p, dest_ip, port);
    pbuf_free(p);
}

// Kompakte JSON-Kodierung der Funktionsaufrufe
void rpc_invoke(const char* func, const char* key, const char* value,
                const ip_addr_t* dest_ip, uint16_t dest_port) {
    char payload[256];
    char method_char;

    if (strcmp(func, "move") == 0) method_char = 'm';
    else if (strcmp(func, "register") == 0) method_char = 'r';
    else if (strcmp(func, "select") == 0) method_char = 's';
    else return;

    // ID erhöhen, einfache Überlaufbehandlung
    if (rpc_id_counter == 0) rpc_id_counter = 1;

    // Erzeuge kompaktes JSON
    snprintf(payload, sizeof(payload),
             "{\"m\":\"%c\",\"p\":{\"%s\":\"%s\"},\"i\":%d}",
             method_char, key, value, rpc_id_counter++);

    rpc_send(payload, dest_ip, dest_port);
}