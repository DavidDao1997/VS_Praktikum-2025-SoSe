#ifndef _REST_LEVEL0_H
#define _REST_LEVEL0_H

#include "ip_addr.h"
#include <stdint.h>

void rest_send_function(const ip_addr_t *dest_ip, uint16_t dest_port, const char *function, const char *params);

#endif //_REST_LEVEL0_H