#include "rest_lvl0.h"
#include "udp_server.h"
#include <stdio.h>
#include <string.h>

void rest_send_function(const ip_addr_t *dest_ip, uint16_t dest_port, const char *function, const char *params)
{
    char buffer[256];
    if (params && strlen(params) > 0) {
        snprintf(buffer, sizeof(buffer), "%s %s", function, params);
    } else {
        snprintf(buffer, sizeof(buffer), "%s", function);
    }
    udp_send_ping(dest_ip, dest_port, buffer);
}