/**
 ******************************************************************************
 * @file   main.c
 * @author  
 * @brief   
 ******************************************************************************
 */
/* Includes ------------------------------------------------------------------*/
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
#include "rpc_client.h"

#define S0 0x00
#define S1 0x01
#define S2 0x02
#define S3 0x03
#define S4 0x04
#define S5 0x05
#define S6 0x06
#define S7 0x07


extern void initITSboard(void);

bool isButtonPressed(uint16_t s);


int main(void) {
  initITSboard(); // Initialisierung des ITS Boards

  GUI_init(DEFAULT_BRIGHTNESS); // Initialisierung des LCD Boards mit Touch
  TP_Init(false);               // Initialisierung des LCD Boards mit Touch

  // Begruessungstext
  lcdPrintlnS("UDP-Client");

  // initialisiere den Stack 
  init_lwip_stack();

  // Setup Interface
  netif_config();
  
  lcdPrintlnS("Initialisiere Netzwerk...");
  
  rpc_client_init();

  lcdPrintlnS("../done");

  // MENUE HERE

  while (1) {
    
    if (isButtonPressed(S0)) { 
      toggleGPIO(&led_pins[0]);
      move(DIR_UP);
      //select(SELECT_UP);
    } else if (isButtonPressed(S1)) {
      toggleGPIO(&led_pins[1]);
      move(DIR_DOWN);
      //select(SELECT_DOWN);
    } else if (isButtonPressed(S2)) {
      toggleGPIO(&led_pins[2]);
      move(DIR_FORWARD);
    } else if (isButtonPressed(S3)) {
      toggleGPIO(&led_pins[3]);
      move(DIR_BACKWARD);
    } else if (isButtonPressed(S4)) {
      toggleGPIO(&led_pins[4]);
      move(DIR_LEFT);
    } else if (isButtonPressed(S5)) {
      toggleGPIO(&led_pins[5]);
      move(DIR_RIGHT);
    } else if (isButtonPressed(S6)) {
      toggleGPIO(&led_pins[6]);
      move(DIR_OPEN);
    } else if (isButtonPressed(S7)) {
      toggleGPIO(&led_pins[7]);
      move(DIR_CLOSE);
    } else {
      // SHOW ERR
      // SELECTION ?
    }

  }
}


bool isButtonPressed(uint16_t s) {
  return (0x01U << s) != ((0x01U << s) & GPIOF -> IDR); 
 }


// EOF
