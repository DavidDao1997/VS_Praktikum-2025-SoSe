#ifndef STATESERVICE_CLIENT_H
#define STATESERVICE_CLIENT_H

#include "lcd.h"
#include "lwip/udp.h"
#include "lwip/pbuf.h"
#include "lwip_interface.h"
#include <stdint.h>
#include <string.h>
#include <stdio.h>
#include "marshall.h"


void invoke_stateService(const char* func, const char* paramTypes[], const char* param[],
                const int numOfParam);


#endif // STATESERVICE_CLIENT_H