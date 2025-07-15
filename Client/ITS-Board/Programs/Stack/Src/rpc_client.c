#include "rpc_client.h"
#include "Driver_Common.h"
#include "err.h"
#include "marshall.h"
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include "caching_proxy.h"
#include "stm32f4xx_hal.h"
#include "timestamp.h"


// #define MYPORT 0xAFFE

//#define DNS_PORT 9000
// #define MAX_RESOLVE_TIME 500

// Clientserver festlegen 
// static ip_addr_t rpc_client_ip;
// Zielserver festlegen 


//static uint32_t last_heartbeat = 0;
//static uint32_t last_timestamp = 0;

//char* socket = "172.16.1.55:54045";


// void set_server_ip(uint8_t a, uint8_t b, uint8_t c, uint8_t d) {
//     IP4_ADDR(&rpc_target_server_ip, a, b, c, d);
// }



// RPC-Client-Port (dynamisch)
// static struct udp_pcb* udp_send_pcb = NULL;
// static uint16_t rpc_id_counter = 1;

// void receive_resolution(uint8_t a, uint8_t b, uint8_t c, uint8_t d, uint32_t port) {
//     set_server_ip( a,  b,  c,  d);
//     rpc_target_server_port = port;
// }

// static void udp_server_recv(void *arg, struct udp_pcb *pcb, struct pbuf *p,
//                             const ip_addr_t *addr, u16_t port) {
//     LWIP_UNUSED_ARG(arg);
//     LWIP_UNUSED_ARG(pcb);

//     if (p != NULL) {
//         /* Empfangene Daten auf dem LCD ausgeben */
//         char buffer[128];
//         memcpy(buffer, p->payload, p->len > 127 ? 127 : p->len);
//         buffer[p->len > 127 ? 127 : p->len] = '\0';

//         char function[64];
//         char params[RPC_MAX_PARAMS][64];   
//         uint32_t timestamp = 0;
//         unmarshall(buffer, function, params, &timestamp);

//         if (strcmp(function, "receiveResolution") == 0) {
//             const char* servicename = params[0];
//             const char* functionname = params[1];
//             const char* full = params[2];  // z.B. "172.16.1.1:123456"

//             if (!((strcmp(full, "") == 0) || (full == NULL))){

//                 if (ERR_OK != cache_store(servicename, functionname, full,  HAL_GetTick())){
//                     lcdPrintlnS("Cache Proxy Full");
//                 }
                
//                 int ip1, ip2, ip3, ip4, port;
//                 if (sscanf(full, "%d.%d.%d.%d:%d", &ip1, &ip2, &ip3, &ip4, &port) == 5) {
//                     receive_resolution(ip1, ip2, ip3, ip4, port);
//                 }
//             }
//         } else if (strcmp(function, "setTimestamp") == 0) {
//             char* service = params[0]; 
//             char* function = params[1];
//             int time;

//             if (sscanf(params[2], "%d", &time) == 1){
//                 set_or_update_timestamp(service, function, time);
//             }
//         }

//         /* Puffer freigeben */
//         pbuf_free(p);
//     }
// }

// Initialisierung des Client-PCB
// int rpc_init(void) {
//     int err = ERR_OK;
//     udp_client_pcb = udp_new();
    
//     // if (udp_client_pcb != NULL) {
//     //     err = udp_bind(udp_client_pcb, IP_ADDR_ANY, 54045);

//     //     if (ERR_OK != err){
//     //         lcdPrintlnS("UDP-Client konnte nicht gestartet werden");
//     //         memp_free(MEMP_UDP_PCB, udp_client_pcb);
//     //         return err;
//     //     } else {
//     //         udp_recv(udp_client_pcb, udp_server_recv, NULL);
//     //     }
//     // }
//     return err;
// }



// Hilfsfunktion zum Senden von RPC-Requests



// void resolve_dns(char* servicename, char* functionname) {
//     set_server_ip(172, 16, 1, 200); 
//     rpc_target_server_port = 9000; 
//     // First search in Cache
//     const char * full = resolve_proxy_dns(servicename, functionname);

//     if (NULL == full){
//         // Not in Cache
//         const char* params[] = { servicename, functionname, socket }; // myIP und Port
//         char payload[PAYLOAD_FIXED_SIZE];
//         uint32_t timestamp = 0;
//         // senden resolve (servicename, funktionsname, Myip:Myport)
//         if (ERR_OK == marshall("resolve", params, 3, payload, timestamp)) {
//             // Sende den Payload an den Server
//             rpc_send(payload);
//         } 

//         uint32_t start = HAL_GetTick();
//         while ((rpc_target_server_port == DNS_PORT) && (HAL_GetTick() - start <= MAX_RESOLVE_TIME)){ //Add time max 500ms
//             check_input();
//         }
//         if (rpc_target_server_port == DNS_PORT){
//            receive_resolution(0, 0, 0, 0, 0);
//         }
//     } else {
//         // Is in cache
//         int ip1, ip2, ip3, ip4, port;
//         if (sscanf(full, "%d.%d.%d.%d:%d", &ip1, &ip2, &ip3, &ip4, &port) == 5) {
//                 receive_resolution(ip1, ip2, ip3, ip4, port);
//         }
//     }
// }


void register_invoke(const char* func, const char* paramTypes[], const char* param[],
                const int numOfParam){
    //char payload[PAYLOAD_FIXED_SIZE];
    uint32_t timestamp = 0;
    bool gotTimestamp = false;
    if (strcmp(func, "select") == 0) {

        get_timestamp("stateService", "select", &timestamp);
        proxy_send("stateService",func, param, numOfParam, timestamp);
        //resolve_dns("stateService", "select"); 
        
        
        //set_server_ip(172, 16, 1, 87); 
        //rpc_target_server_port = 63721; 

    } else if (strcmp(func, "move") == 0) {

        get_timestamp("moveAdapter", "move", &timestamp);
        proxy_send("moveAdapter",func, param, numOfParam, timestamp);
        //resolve_dns("moveAdapter", "move"); 
        

        //set_server_ip(172, 16, 1, 87); 
        //rpc_target_server_port = 63721; 

    } else if (strcmp(func, "setTimestamp") == 0) {
        get_timestamp("moveAdapter", "move", &timestamp);
        proxy_send("dns",func, param, numOfParam, timestamp);
    
    }
    // if (gotTimestamp){
    //     marshall(func, param, numOfParam, payload, timestamp);
    //     rpc_send(payload);
    // }
}



// NOT USED YET-------------------------------------------------


// void rpc_send_heartbeat(uint32_t now) {
//     if (now - last_heartbeat > 100) {
//         err_t err = ERR_OK;//dns_gethostbyname(watchdog_hostname, &rpc_server_ip, dns_found_cb, NULL);
//         if (err == ERR_OK) {
        
//             last_heartbeat = now;
        
//             // IP war schon gecached, direkt senden
//             const char* params[] = { "IO" };
//             char payload[PAYLOAD_FIXED_SIZE];
//             uint32_t timestamp = 0;
//             marshall("heartbeat", params, 1, payload, timestamp);


//             set_server_ip(172, 16, 1, 87);
//             rpc_target_server_port =0xAFFA; 
//             rpc_send(payload);
//         }
//     }
// }

// void rpc_send_timestamp(uint32_t now) {
//     if (now - last_timestamp > 250) {
//         err_t err = ERR_OK;
//         if (err == ERR_OK) {
        
//             last_timestamp = now;
        
//             char act_timestamp[16];
//             sprintf(act_timestamp, "%d", now);
//             // IP war schon gecached, direkt senden
//             const char* params[] = { "IO", act_timestamp};
//             char payload[PAYLOAD_FIXED_SIZE];
//             uint32_t timestamp = 0;
//             uint32_t gotTimestamp = 0;     
            
//             // set timestamp to needed services
//             if (get_timestamp("moveAdapter","setTimestamp", &timestamp)){
//                 marshall("setTimestamp", params, 2, payload, timestamp);
//                 resolve_dns("moveAdapter", "setTimestamp");
//                 rpc_send(payload);
//             }
//             if (get_timestamp("stateService", "setTimestamp", &timestamp)){
//                 marshall("setTimestamp", params, 2, payload, timestamp);
//                 resolve_dns("stateService", "setTimestamp"); 
//                 rpc_send(payload);
//             }
//         }
//     }
// }

// ----------------------------------------------------