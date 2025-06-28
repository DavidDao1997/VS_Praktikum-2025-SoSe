#ifndef MARSHALL_H
#define MARSHALL_H

#include <string.h>
#include <stdio.h>
#include "idl.h"


void marshall(const char* func, const char* paramTypes[], const char* param[],
                const int numOfParam, char* payload );

void unmarshall();


#endif //MARSHALL_H