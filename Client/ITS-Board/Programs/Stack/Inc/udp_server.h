
 #ifndef _UDP_SERVER_H
 #define _UDP_SERVER_H


 #include "ip_addr.h"

/**
  * @brief  Initialisiert den UDP-Server
  * @param  None
  * @retval None
  */

void udp_server_init(void);
void udp_send_ping(const ip_addr_t *dest_ip, u16_t dest_port, const char *msg);


 #endif //_UDP_SERVER_H