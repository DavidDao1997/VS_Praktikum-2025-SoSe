#ifndef RPC_CLIENT_H
#define RPC_CLIENT_H

#include "lcd.h"
#include "lwip/udp.h"
#include "lwip/pbuf.h"
#include <string.h>
#include <stdio.h>

void rpc_client_init(void);
void rpc_invoke(const char* func, const char* key, const char* value,
                const ip_addr_t* dest_ip, uint16_t dest_port);


#endif // RPC_CLIENT_H