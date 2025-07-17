/**
* 
*/


#ifndef CACHING_PROXY_H
#define CACHING_PROXY_H

#include "marshall.h"
#include "lwip/udp.h"
#include "lwip_interface.h"
#include <stdint.h>

int rpc_proxy_init(void);

void proxy_send(const char * servicename, const char* function, const char* param[],
            const int numOfParam, uint32_t timestamp );


void receive_resolution(uint8_t a, uint8_t b, uint8_t c, uint8_t d, uint32_t port);
int cache_store(const char* servicename, const char* functionname, const char* socket, uint32_t time);

#endif //CACHING_PROXY_H