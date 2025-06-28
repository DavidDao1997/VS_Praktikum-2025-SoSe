#include "rpc_client.h"





// Zielserver festlegen 
static ip_addr_t rpc_client_ip;


static ip_addr_t rpc_server_ip;
static uint16_t rpc_server_port = 0xAFFE;

void app_stub_set_server_ip(uint8_t a, uint8_t b, uint8_t c, uint8_t d) {
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

        lcdPrintS("Empfangen: ");
        lcdPrintlnS(buffer);

        /* Echo die Daten zurück */
        udp_sendto(pcb, p, addr, port);

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

            lcdPrintlnS("UDP-Server gestartet auf Port 7");

            /* Optional: Sende eine Begrüßungsnachricht an einen Standard-Client */
            // struct pbuf *p = pbuf_alloc(PBUF_TRANSPORT, strlen(hello_msg), PBUF_RAM);
            // if (p != NULL) {
            //     memcpy(p->payload, hello_msg, strlen(hello_msg));
            //     ip_addr_t dest_ip;
            //     IP4_ADDR(&dest_ip, 192,168,0,100); // Beispiel-IP
            //     udp_sendto(udp_server_pcb, p, &dest_ip, UDP_SERVER_PORT);
            //     pbuf_free(p);
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


    marshall(func, paramTypes, param, numOfParam, payload);

    app_stub_set_server_ip(192, 168, 33, 1); // TODO DNS
    rpc_server_port = 0xAFFF; // TODO DNS


    rpc_send(payload);
}