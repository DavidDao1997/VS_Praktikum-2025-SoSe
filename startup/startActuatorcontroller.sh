#!/bin/bash

# Prüfen, ob X (z. B. "2" in R2A1) als Argument übergeben wurde
if [ $# -ne 2 ]; then
    echo "Usage: $0 <X-Wert für RXYA, z. B. 2> <IP_ADDR>"
    exit 1
fi

X="$1"
IP_ADDR="$2"
# Prüfen, ob X eine Zahl ist
if ! [[ "$X" =~ ^[0-9]+$ ]]; then
    echo "Fehler: X muss eine Zahl sein (z. B. 2 für R2A1-R2A4)"
    exit 1
fi

# Feste Werte
DNS_SOCKET="172.16.1.87:9000"

# Array für gestartete PIDs
PIDS=()

# Schleife für vier Prozesse
for i in {0..3}; do
    PORT=$((5005 + i))
    DNS_CALLBACK_PORT=$((8005 + i))
    Y=$((i + 1))
    RXYA="R${X}A${Y}"

    # Werte für R und A extrahieren
    R_VALUE="$X"
    A_VALUE="$Y"

    echo "➡️  Starte actuatorcontroller: PORT=$PORT, DNS_CALLBACK_PORT=$DNS_CALLBACK_PORT, RXYA=$RXYA (→ $R_VALUE $A_VALUE)"

    # Starte Java-Prozess im Hintergrund
    PORT=$PORT \
    IP_ADDR=$IP_ADDR \
    DNS_CALLBACK_PORT=$DNS_CALLBACK_PORT \
    DNS_SOCKET=$DNS_SOCKET \
    java -jar actuatorcontroller-v2.jar "$R_VALUE" "$A_VALUE" &

    PID=$!
    PIDS+=($PID)
    echo "   ✅ gestartet mit PID=$PID"
done

# Trap zum Beenden aller gestarteten Prozesse
# trap "echo '🛑 Beende alle gestarteten Prozesse...'; kill ${PIDS[@]}; exit" INT TERM EXIT

# Warten auf alle Prozesse (optional: remove `wait` to not block)
# wait
# Function to handle script termination

CLEANED_UP=0  # Guard variable

cleanup() {
    if [[ $CLEANED_UP -eq 1 ]]; then
        return
    fi
    CLEANED_UP=1
    echo -e "\n🛑 Caught signal, terminating started processes..."
    for pid in "${PIDS[@]}"; do
        if kill -0 "$pid" 2>/dev/null; then
            echo "   🔪 Killing PID $pid"
            kill "$pid"
        fi
    done
    exit 0
}

# Set trap for INT (Ctrl+C), TERM, and EXIT
trap cleanup INT TERM EXIT
wait
