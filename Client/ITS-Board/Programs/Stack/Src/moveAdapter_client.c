#include "Driver_Common.h"
#include "err.h"
#include "marshall.h"
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include "caching_proxy.h"
#include "stm32f4xx_hal.h"
#include "timestamp.h"
#include "moveAdapter_client.h"



void invoke_moveAdapter(const char* func, const char* paramTypes[], const char* param[],
                const int numOfParam){
    uint32_t timestamp = 0;
    if (strcmp(func, "move") == 0) {

        get_timestamp("moveAdapter", "move", &timestamp);
        proxy_send("moveAdapter",func, param, numOfParam, timestamp);
        
    } 
    // else if (strcmp(func, "any") == 0) {


    // }
    
}



