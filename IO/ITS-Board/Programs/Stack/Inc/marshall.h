#ifndef MARSHALL_H
#define MARSHALL_H

#include <stdint.h>
#include <string.h>
#include <stdio.h>
#include "idl.h"

#define PAYLOAD_FIXED_SIZE 256

int marshall(const char* func, const char* param[],
                const int numOfParam, char* payload, uint32_t timestamp );

void unmarshall(const char* payload, char* function, char params[][64], uint32_t* timestamp);


#endif //MARSHALL_H