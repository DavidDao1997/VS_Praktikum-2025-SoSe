#include "timestamp.h"
#include <string.h>
#include <stdbool.h> // Für die Verwendung von true/false

#define CACHE_SIZE 255

// Definieren der Struktur
typedef struct {
    char servicename[32];
    char function[32];
    uint32_t timestamp;
} timestamp_entry_t;

// Durch die explizite Initialisierung mit {0} wird der gesamte Cache
// beim Programmstart garantiert mit Nullen gefüllt.
// Jeder servicename ist also anfangs ein leerer String ("").
static timestamp_entry_t cache[CACHE_SIZE] = {0};

/**
 * @brief Aktualisiert den Timestamp für einen Service oder fügt ihn hinzu, wenn er nicht existiert.
 *
 * @param servicename Der Name des Services. Darf nicht NULL sein.
 * @param timestamp Der zu setzende Zeitstempel.
 * @return true, wenn der Eintrag erfolgreich gesetzt/aktualisiert wurde.
 * @return false, wenn der Cache voll war und kein neuer Eintrag hinzugefügt werden konnte.
 */
bool set_or_update_timestamp(const char *servicename, const char* function, uint32_t timestamp) {
    if (servicename == NULL || function == NULL) {
        return false;
    }

    int first_free_slot = -1;

    // Durchlaufe den Cache, um einen vorhandenen Eintrag oder den ersten freien Platz zu finden
    for (int i = 0; i < CACHE_SIZE; ++i) {
        // 1. Prüfen, ob der Eintrag bereits existiert
        if (strcmp(cache[i].servicename, servicename) == 0 && strcmp(cache[i].function, function) == 0) {
            cache[i].timestamp = timestamp;
            return true;
        }

        // 2. Merken, wo der erste freie Platz ist
        if (cache[i].servicename[0] == '\0' && cache[i].function[0] == '\0' && first_free_slot == -1) {
            first_free_slot = i;
        }
    }

    // Wenn wir hier ankommen, existiert der Eintrag noch nicht.
    // Prüfen, ob wir einen freien Platz gefunden haben.
    if (first_free_slot != -1) {
        strncpy(cache[first_free_slot].servicename, servicename, sizeof(cache[first_free_slot].servicename) - 1);
        strncpy(cache[first_free_slot].function, function, sizeof(cache[first_free_slot].function) - 1);
        // Sicherstellen, dass der String immer null-terminiert ist, falls servicename zu lang war.
        cache[first_free_slot].servicename[sizeof(cache[first_free_slot].servicename) - 1] = '\0';
        cache[first_free_slot].function[sizeof(cache[first_free_slot].function) - 1] = '\0';
        cache[first_free_slot].timestamp = timestamp;
        return true;
    }
    // Kein vorhandener Eintrag und kein freier Platz gefunden. Der Cache ist voll.
    return false;
}

/**
 * @brief Ruft den Timestamp für einen bestimmten Service ab.
 *
 * @param servicename Der Name des Services. Darf nicht NULL sein.
 * @param out_timestamp Ein Zeiger, in den der gefundene Zeitstempel geschrieben wird.
 * @return true, wenn der Service gefunden wurde.
 * @return false, wenn der Service nicht im Cache existiert.
 */
bool get_timestamp(const char *servicename, const char* function, uint32_t *out_timestamp) {
    if (servicename == NULL || function == NULL || out_timestamp == NULL) {
        return false;
    }
    for (int i = 0; i < CACHE_SIZE; i++) {
        if (strcmp(cache[i].servicename, servicename) == 0 && strcmp(cache[i].function, function) == 0) {
            *out_timestamp = cache[i].timestamp;
            return true;
        }
    }
    return false;
}