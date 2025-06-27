#ifndef RPC_CLIENT_H
#define RPC_CLIENT_H

#include "lcd.h"
#include "lwip/udp.h"
#include "lwip/pbuf.h"
#include <string.h>
#include <stdio.h>
#include "marshall.h"

void rpc_client_init(void);
void rpc_invoke(const char* func, const char* paramTypes[], const char* param[],
                const int numOfParam);


#endif // RPC_CLIENT_H