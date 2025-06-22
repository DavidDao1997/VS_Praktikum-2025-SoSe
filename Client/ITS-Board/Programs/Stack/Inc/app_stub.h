#ifndef APP_STUB_H
#define APP_STUB_H

typedef enum {
    DIR_UP,
    DIR_DOWN,
    DIR_LEFT,
    DIR_RIGHT,
    DIR_FORWARD,
    DIR_BACKWARD,
    DIR_OPEN,
    DIR_CLOSE
} Direction;

typedef enum {
    SELECT_UP,
    SELECT_DOWN
} SDirection;


void move(Direction dir);
void register_node(const char* name);
void select(SDirection dir);

#endif //APP_STUB_H