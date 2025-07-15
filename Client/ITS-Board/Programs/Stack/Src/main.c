/**
 ******************************************************************************
 * @file   main.c
 * @author  
 * @brief   
 ******************************************************************************
 */
/* Includes ------------------------------------------------------------------*/
#include "delay.h"
#include "err.h"
#include <ctype.h>
#include <stdbool.h>
#define STM32F429xx
#include <arm_compat.h>

#include "LCD_GUI.h"
#include "stm32f4xx_hal.h"
#include <stdint.h>

#include "LCD_GUI.h"
#include "LCD_Touch.h"

#include "lcd.h"
#include "led.h"

#include "lwip_interface.h"
#include "app_stub.h"
//#include "rpc_client.h"
#include "moveAdapter_client.h"
#include "stateService_client.h"
#include "rpc_server.h"
#include "caching_proxy.h"

#define S0 0x00
#define S1 0x01
#define S2 0x02
#define S3 0x03
#define S4 0x04
#define S5 0x05
#define S6 0x06
#define S7 0x07

extern void initITSboard(void);

typedef enum {
  STATE_SELECT_MENU,
  STATE_MOVE_MENU,
  STATE_CLAW_MENU,
  STATE_COUNT
} State;

State currentState = STATE_SELECT_MENU;
State lastState = STATE_SELECT_MENU;

typedef struct {
  bool S0_pressed;
  bool S1_pressed; // NOT NEEDED YET
  bool S2_pressed;
  bool S3_pressed;
  bool S4_pressed;
  bool S5_pressed;
  bool S6_pressed;
  bool S7_pressed;
} ButtonState;

bool isButtonPressed(uint16_t s) {
  return (0x01U << s) != ((0x01U << s) & GPIOF -> IDR); 
 }

bool isAnyButtonPressed(ButtonState btns){
  return btns.S0_pressed ||
         btns.S1_pressed ||
         btns.S2_pressed ||
         btns.S3_pressed ||
         btns.S4_pressed ||
         btns.S5_pressed ||
         btns.S6_pressed ||
         btns.S7_pressed;
}




void readButtons(ButtonState *buttons) {
  buttons->S0_pressed = isButtonPressed(S0);
  buttons->S1_pressed = isButtonPressed(S1);
  buttons->S2_pressed = isButtonPressed(S2);
  buttons->S3_pressed = isButtonPressed(S3);
  buttons->S4_pressed = isButtonPressed(S4);
  buttons->S5_pressed = isButtonPressed(S5);
  buttons->S6_pressed = isButtonPressed(S6);
  buttons->S7_pressed = isButtonPressed(S7);
}

void toggleState() {
  currentState = (currentState + 1) % STATE_COUNT;
}

void resetScreen() {
  GUI_clear(WHITE);
  lcdGotoXY(1, 1);
}


int main(void) {
  initITSboard(); // Initialisierung des ITS Boards

  GUI_init(DEFAULT_BRIGHTNESS); // Initialisierung des LCD Boards mit Touch
  TP_Init(false);               // Initialisierung des LCD Boards mit Touch

  // Begruessungstext
  lcdPrintlnS("Roboterarm-Steuerung");

  // initialisiere den Stack 
  init_lwip_stack();

  // Setup Interface
  netif_config();
  
  lcdPrintlnS("Initialisiere Netzwerk...");
  
  //int err = rpc_init();
  int err_proxy = rpc_proxy_init();
  int err_server = rpc_server_init();

  if ((err_proxy != ERR_OK) && (err_server != ERR_OK)) {
    lcdPrintlnS("Fehler beim Starten der RPC Funktion");
    while (1) {
      // Endlosschleife bei Fehler
    }
  }

  register_node( "IO", "setTimestamp");
  lcdPrintlnS("../done");


  resetScreen();
 

   ButtonState btn, lastBtn = {0};

  // INITIAL MENUE HERE
  lcdPrintlnS("SELECT MENU");
  lcdPrintlnS("S7 -> UP");
  lcdPrintlnS("S6 -> DOWN");
  lcdPrintlnS("S0 -> NEXT MENU");

  uint32_t last_cycle = 0;

  while (1) {
    uint32_t now = HAL_GetTick();
    check_input(); // Check for incoming packets
    //rpc_send_heartbeat(now); // Send heartbeat to server
    //rpc_send_timestamp(now);

    
    if (now - last_cycle >= 20) {
      last_cycle = now;
      //
      // === READ ===
      //
      readButtons(&btn);

      //
      // === MODIFY ===
      //
      if (!isAnyButtonPressed(lastBtn)) {
        if (btn.S0_pressed && !lastBtn.S0_pressed) {
          toggleState();  // FSM-State Wechsel bei S0
        }

        //
        // === WRITE ===
        //
      
        if (currentState != lastState) {
          resetScreen();
          // Zustandswechsel erkannt → ggf. Statusanzeige
          switch (currentState) {
            case STATE_SELECT_MENU:
              lcdPrintlnS("SELECT MENU");
              lcdPrintlnS("S7 -> UP");
              lcdPrintlnS("S6 -> DOWN");
              lcdPrintlnS("S0 -> NEXT MENU");
              break;
            case STATE_MOVE_MENU:
              lcdPrintlnS("MOVE MENU");
              lcdPrintlnS("S7 -> UP");
              lcdPrintlnS("S6 -> DOWN");
              lcdPrintlnS("S5 -> FORWARD");
              lcdPrintlnS("S4 -> BACKWARD");
              lcdPrintlnS("S3 -> LEFT");
              lcdPrintlnS("S2 -> RIGHT");
              lcdPrintlnS("S0 -> NEXT MENU");
              break;
            case STATE_CLAW_MENU:
              lcdPrintlnS("CLAW MENU");
              lcdPrintlnS("S7 -> OPEN");
              lcdPrintlnS("S6 -> CLOSE");
              lcdPrintlnS("S0 -> NEXT MENU");
              break;
            default:
              break;
          }

          lastState = currentState;  // Aktuellen Zustand merken
        }

      
        switch (currentState) {
          case STATE_SELECT_MENU:
            if (btn.S7_pressed && !lastBtn.S7_pressed) {
              select(SELECT_UP);
            } else if (btn.S6_pressed && !lastBtn.S6_pressed) {
              select(SELECT_DOWN);
            }
            break;

          case STATE_MOVE_MENU:
            if (btn.S7_pressed && !lastBtn.S7_pressed) {
              move(DIR_UP);
            } else if (btn.S6_pressed && !lastBtn.S6_pressed) {
              move(DIR_DOWN);
            } else if (btn.S5_pressed && !lastBtn.S5_pressed) {
              move(DIR_FORWARD);
            } else if (btn.S4_pressed && !lastBtn.S4_pressed) {
              move(DIR_BACKWARD);
            } else if (btn.S3_pressed && !lastBtn.S3_pressed) {
              move(DIR_LEFT);
            } else if (btn.S2_pressed && !lastBtn.S2_pressed) {
              move(DIR_RIGHT);
            } 
            break;

          case STATE_CLAW_MENU:
            if (btn.S7_pressed && !lastBtn.S7_pressed) {
              move(DIR_OPEN);
            } else if (btn.S6_pressed && !lastBtn.S6_pressed) {
              move(DIR_CLOSE);
            }        
            break;

          default:
            break;
        } 
      }
      lastBtn = btn;
    }
  }
}





// EOF
