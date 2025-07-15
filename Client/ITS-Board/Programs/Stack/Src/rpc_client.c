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


void register_invoke(const char* func, const char* paramTypes[], const char* param[],
                const int numOfParam){
    uint32_t timestamp = 0;
    
    if (strcmp(func, "register") == 0) {
        //get_timestamp("moveAdapter", "move", &timestamp);
        proxy_send("dns",func, param, numOfParam, timestamp);
    }
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