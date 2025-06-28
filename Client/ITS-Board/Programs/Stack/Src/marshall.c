
    
    
    
  void marshall(const char* func, const char* paramTypes, const char* param,
                const int numOfParam, const char* payload ) {  
    
    payload[0] = '\0';

    const RpcFunction* fdef = find_rpc_function(func);
    if (!fdef) {
        snprintf(payload, 256, "{\"error\": \"unknown function '%s'\"}", func);
        return;
    }

    if (fdef->numParams != numOfParam) {
        snprintf(payload, 256, "{\"error\": \"wrong number of parameters for '%s'\"}", func);
        return;
    }

    strcat(payload, "{\"function\": \"");
    strcat(payload, func);
    strcat(payload, "\", \"params\": {");

    if (strcmp(func, "updateView") == 0) {
        const char** listOfR = (const char**)param[0];
        int listLen = 0;
        while (listOfR[listLen] != NULL && listLen < 256) listLen++;

        uint32_t bitmap[8];
        encode_rlist_to_bitmap(listOfR, listLen, bitmap);

        strcat(payload, "\"bitmap\": [");
        char tmp[16];
        for (int i = 0; i < 8; ++i) {
            snprintf(tmp, sizeof(tmp), "%u", bitmap[i]);
            strcat(payload, tmp);
            if (i < 7) strcat(payload, ", ");
        }

        strcat(payload, "], \"index\": ");
        strcat(payload, param[1]);
        strcat(payload, ", \"conf\": ");
        strcat(payload, strcmp(param[2], "1") == 0 ? "true" : "false");
        strcat(payload, ", \"err\": ");
        strcat(payload, strcmp(param[3], "1") == 0 ? "true" : "false");
        strcat(payload, "}}");
        return;
    }

    // Generisches Marshalling für andere Funktionen
    strcat(payload, "\"values\": [");
    for (int i = 0; i < numOfParam; ++i) {
        strcat(payload, "\"");
        strcat(payload, param[i]);
        strcat(payload, "\"");
        if (i < numOfParam - 1) strcat(payload, ", ");
    }
    strcat(payload, "]}");


  }


  