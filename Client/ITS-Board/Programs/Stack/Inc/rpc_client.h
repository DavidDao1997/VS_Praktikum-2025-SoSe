#ifndef RPC_CLIENT_H
#define RPC_CLIENT_H

#include "lcd.h"
#include "lwip/udp.h"
#include "lwip/pbuf.h"
#include "lwip_interface.h"
#include <string.h>
#include <stdio.h>
#include "marshall.h"

int rpc_init(void);
void rpc_invoke(const char* func, const char* paramTypes[], const char* param[],
                const int numOfParam);

void rpc_send_heartbeat(uint32_t now);


#endif // RPC_CLIENT_H