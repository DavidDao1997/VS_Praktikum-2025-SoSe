

#include "marshall.h"
#include <string.h>


void unmarshall(const char* payload, char* function, char params[][64]) {
    function[0] = '\0';
    // Funktionsnamen extrahieren
    const char* func_start = strstr(payload, "{\"function\":\"");
    if (!func_start) return;
    func_start += strlen("{\"function\":\"");
    const char* func_end = strchr(func_start, '"');
    if (!func_end) return;
    size_t func_len = func_end - func_start;
    strncpy(function, func_start, func_len);
    function[func_len] = '\0';

    // Hole Funktionsdefinition aus der IDL-Tabelle
    const RpcFunction* fdef = find_rpc_function(function);
    if (!fdef) return;
    // Parameter extrahieren
    const char* params_start = strstr(payload, "\"params\":[");
    if (!params_start) return;
    
    params_start += strlen("\"params\":[");
    const char* params_end = strchr(params_start, ']');
    if (!params_end) return;
    
    char params_buf[256];
    size_t params_len = params_end - params_start;
    strncpy(params_buf, params_start, params_len);
    params_buf[params_len] = '\0';

    int count = 0;
    char* token = strtok(params_buf, ",");
    while (token && count < fdef->numParams) {
        // Entferne führende und abschließende Leerzeichen/Anführungszeichen
        while (*token == ' ' || *token == '\"') token++;
        char* end = token + strlen(token) - 1;
        while (end > token && (*end == ' ' || *end == '\"')) {
            *end = '\0';
            end--;
        }
        strncpy(params[count], token, 63);
        params[count][63] = '\0';
        count++;
        token = strtok(NULL, ",");
    }
}
    
int marshall(const char* func, const char* param[],
            const int numOfParam, char* payload ) {  

    payload[0] = '\0';

    const RpcFunction* fdef = find_rpc_function(func);
    if (!fdef) {
        snprintf(payload, 256, "{\"error\": \"unknown function '%s'\"}", func);
        return -1;
    }

    if (fdef->numParams != numOfParam) {
        snprintf(payload, 256, "{\"error\": \"wrong number of parameters for '%s'\"}", func);
        return -1;
    }

    strcat(payload, "{\"function\": \"");
    strcat(payload, func);
    strcat(payload, "\", \"params\": ");

    strcat(payload, "[");
    for (int i = 0; i < numOfParam; ++i) {
        if (strcmp(fdef->paramTypes[i], "String") == 0 ){
            strcat(payload, "\"");
            strcat(payload, param[i]);
            strcat(payload, "\"");
        } else {
            strcat(payload, param[i]);
        }
        if (i < numOfParam - 1) strcat(payload, ", ");
    }
    strcat(payload, "]}");

    return 0;
}


  