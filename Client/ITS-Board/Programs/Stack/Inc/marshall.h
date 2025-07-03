#ifndef MARSHALL_H
#define MARSHALL_H

#include <string.h>
#include <stdio.h>
#include "idl.h"



int marshall(const char* func, const char* param[],
                const int numOfParam, char* payload );

void unmarshall(const char* payload, char* function, char params[][64]);


#endif //MARSHALL_H