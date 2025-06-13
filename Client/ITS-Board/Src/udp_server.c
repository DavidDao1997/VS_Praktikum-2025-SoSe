#include <string.h>
#include "lcd.h"
#include "lwip/udp.h"
#include "udp_server.h"

/* Definiere UDP Server Port */
#define UDP_SERVER_PORT    0xAFFE /* Echo-Port */

/* UDP-Serverstruktur */
struct udp_pcb *udp_server_pcb;

/* Puffer für die Datenübertragung */
static char hello_msg[] = "Hello World from ITS-Board UDP Server!";

/* Callback-Funktion für empfangene UDP-Pakete */
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

/* Initialisiert den UDP-Server */
void udp_server_init(void)
{
    /* Erstelle neuen UDP PCB */
    udp_server_pcb = udp_new();

    if (udp_server_pcb != NULL) {
        err_t err;

        /* Binde den PCB an den Port */
        err = udp_bind(udp_server_pcb, IP_ADDR_ANY, UDP_SERVER_PORT);

        if (err == ERR_OK) {
            /* Registriere Callback für empfangene Pakete */
            udp_recv(udp_server_pcb, udp_server_recv, NULL);

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
            memp_free(MEMP_UDP_PCB, udp_server_pcb);
        }
    }
}

void udp_send_ping(const ip_addr_t *dest_ip, u16_t dest_port, const char *msg)
{
    if (udp_server_pcb == NULL) return;

    struct pbuf *p = pbuf_alloc(PBUF_TRANSPORT, strlen(msg), PBUF_RAM);
    if (p != NULL) {
        memcpy(p->payload, msg, strlen(msg));
        udp_sendto(udp_server_pcb, p, dest_ip, dest_port);
        pbuf_free(p);
    }
}