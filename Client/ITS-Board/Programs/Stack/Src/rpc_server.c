#include "rpc_server.h"
#include <string.h>
#include "err.h"
#include "marshall.h"
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include "caching_proxy.h"
#include "stm32f4xx_hal.h"
#include "timestamp.h"
#include "lcd.h"

#define MYPORT 0xAFFE
static struct udp_pcb* udp_server_pcb = NULL;

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
        uint32_t timestamp = 0;
        unmarshall(buffer, function, params, &timestamp);

        if (strcmp(function, "receiveResolution") == 0) {
            const char* servicename = params[0];
            const char* functionname = params[1];
            const char* full = params[2];  // z.B. "172.16.1.1:123456"

            if (!((strcmp(full, "") == 0) || (full == NULL))){

                if (ERR_OK != cache_store(servicename, functionname, full,  HAL_GetTick())){
                    lcdPrintlnS("Cache Proxy Full");
                }
                
                int ip1, ip2, ip3, ip4, port;
                if (sscanf(full, "%d.%d.%d.%d:%d", &ip1, &ip2, &ip3, &ip4, &port) == 5) {
                    receive_resolution(ip1, ip2, ip3, ip4, port);
                }
            }
        } else if (strcmp(function, "setTimestamp") == 0) {
            char* service = params[0]; 
            char* function = params[1];
            int time;

            if (sscanf(params[2], "%d", &time) == 1){
                set_or_update_timestamp(service, function, time);
            }
        }

        /* Puffer freigeben */
        pbuf_free(p);
    }
}


int rpc_server_init(void) {
    int err = ERR_OK;
    udp_server_pcb = udp_new();
    
    if (udp_server_pcb != NULL) {
        err = udp_bind(udp_server_pcb, IP_ADDR_ANY, 0xAFFE);

        if (ERR_OK != err){
            lcdPrintlnS("UDP-Server konnte nicht gestartet werden");
            memp_free(MEMP_UDP_PCB, udp_server_pcb);
            return err;
        } else {
            udp_recv(udp_server_pcb, udp_server_recv, NULL);
        }
    }
    return err;
}