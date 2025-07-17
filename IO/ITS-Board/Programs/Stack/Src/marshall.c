

#include "marshall.h"
#include <stdint.h>
#include <string.h>


/**
 * @brief Parst einen JSON-Payload.
 *
 * @param payload Der zu parsende JSON-String.
 * @param function Ausgabepuffer für den Funktionsnamen. Ist nach einem Fehler leer.
 * @param params Ausgabearray für die Parameter.
 * @param timestamp Ausgabeparameter für den Zeitstempel.
 */
void unmarshall(const char* payload, char* function, char params[][64], uint32_t* timestamp) {
    // 1. Setze alle Ausgabeparameter in einen definierten "Fehler"-Zustand.
    
    function[0] = '\0';
    *timestamp = 0;
    
    if (!payload) {
        return;
    }

    const char* p = payload;
    char* end_ptr;

    // --- 2. Timestamp extrahieren ---
    const char* ts_key = "\"timeStamp\":";
    p = strstr(p, ts_key);
    if (!p) return; 
    p += strlen(ts_key);

    *timestamp = strtoul(p, &end_ptr, 10);
    if (p == end_ptr) return; 
    p = end_ptr;

    // --- 3. Funktionsnamen extrahieren ---
    const char* func_key = "\"function\":\"";
    p = strstr(p, func_key);
    if (!p) return; 
    p += strlen(func_key);

    const char* func_end = strchr(p, '"');

    size_t func_len = func_end - p;
    // Annahme: der 'function'-Puffer ist 64 Bytes groß
    if (func_len >= 64) func_len = 63;
    strncpy(function, p, func_len);
    function[func_len] = '\0'; 
    p = func_end;

    // --- 4. Funktionsdefinition prüfen ---
    const RpcFunction* fdef = find_rpc_function(function);
    if (!fdef) {
        function[0] = '\0';
        return; 
    }

    // --- 5. Parameter extrahieren ---
    const char* params_key = "\"params\":[";
    p = strstr(p, params_key);
    if (!p) { function[0] = '\0'; return; } 
    p += strlen(params_key);

    const char* params_end = strchr(p, ']');
    if (!params_end) { function[0] = '\0'; return; } 
    char params_buf[256];
    size_t params_len = params_end - p;
    if (params_len >= sizeof(params_buf)) params_len = sizeof(params_buf) - 1;
    strncpy(params_buf, p, params_len);
    params_buf[params_len] = '\0';

    int count = 0;
    char* token = strtok(params_buf, ",");
    while (token && count < fdef->numParams) {
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
    

/**
 * @brief Erstellt einen JSON-Payload. Arbeitet mit einer festen Puffergröße (PAYLOAD_FIXED_SIZE).
 * @param func Funktionsname.
 * @param param Array von Parametern als Strings.
 * @param numOfParam Anzahl der Parameter.
 * @param payload Der Puffer, in den geschrieben wird (Größe MUSS PAYLOAD_FIXED_SIZE sein).
 * @param timestamp Der Zeitstempel, der eingefügt werden soll.
 * @return 0 bei Erfolg, -1 bei Fehler (z.B. Puffer zu klein, unbekannte Funktion).
 */
int marshall(const char* func, const char* param[],
            const int numOfParam, char* payload, uint32_t timestamp) {

    // --- Fehlerprüfungen am Anfang ---
    const RpcFunction* fdef = find_rpc_function(func);
    if (!fdef) {
        // Sicherer Weg, eine Fehlermeldung zu schreiben
        snprintf(payload, PAYLOAD_FIXED_SIZE, "{\"error\":\"unknown function '%s'\"}", func);
        return -1;
    }

    if (fdef->numParams != numOfParam) {
        snprintf(payload, PAYLOAD_FIXED_SIZE, "{\"error\":\"wrong number of parameters for '%s'\"}", func);
        return -1;
    }

    // --- Sicherer Aufbau des JSON-Strings ---
    char* p = payload;
    size_t remaining_size = PAYLOAD_FIXED_SIZE;
    int written;

    // Beginne das JSON-Objekt mit Timestamp und Funktion
    written = snprintf(p, remaining_size, "{\"timeStamp\":%u,\"function\":\"%s\",\"params\":[", timestamp, func);

    // Prüfen, ob snprintf einen Fehler hatte oder der Puffer zu klein war
    if (written < 0 || written >= remaining_size) {
        payload[0] = '\0'; // Puffer leeren, um ungültigen Teilstring zu vermeiden
        return -1;
    }

    // Zeiger und verbleibende Größe aktualisieren
    p += written;
    remaining_size -= written;

    // Füge die Parameter hinzu
    for (int i = 0; i < numOfParam; ++i) {
        // Parameter (als String oder als numerischer Wert)
        if (strcmp(fdef->paramTypes[i], "String") == 0) {
            written = snprintf(p, remaining_size, "\"%s\"", param[i]);
        } else {
            written = snprintf(p, remaining_size, "%s", param[i]);
        }
        if (written < 0 || written >= remaining_size) { payload[0] = '\0'; return -1; }
        p += written;
        remaining_size -= written;

        // Füge Komma hinzu, wenn es nicht der letzte Parameter ist
        if (i < numOfParam - 1) {
            written = snprintf(p, remaining_size, ",");
            if (written < 0 || written >= remaining_size) { payload[0] = '\0'; return -1; }
            p += written;
            remaining_size -= written;
        }
    }

    // Schließe das Array und das Objekt
    written = snprintf(p, remaining_size, "]}");
    if (written < 0 || written >= remaining_size) { payload[0] = '\0'; return -1; }

    return 0; // Erfolg
}




  