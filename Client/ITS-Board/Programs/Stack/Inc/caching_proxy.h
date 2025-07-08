#ifndef CACHING_PROXY_H
#define CACHING_PROXY_H

#include "marshall.h"
#include "lwip/udp.h"
#include "lwip_interface.h"
#include <stdint.h>


const char* resolve_proxy_dns(char* servicename, char* functionname);
int cache_store(const char* servicename, const char* functionname, const char* socket, uint32_t time);

#endif //CACHING_PROXY_H