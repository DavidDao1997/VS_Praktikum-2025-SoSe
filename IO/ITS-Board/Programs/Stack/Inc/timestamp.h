#ifndef TIMESTAMP_H
#define TIMESTAMP_H

#include <stdint.h>
#include <stdbool.h>


bool set_or_update_timestamp(const char *servicename, const char* function, uint32_t timestamp);
bool get_timestamp(const char *servicename, const char* function, uint32_t *out_timestamp);

#endif //TIMESTAMP_H