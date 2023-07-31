#ifndef LBASELIB_H
#define LBASELIB_H

int
luaB_print(lua_State *L);

/*
** Creates a warning with all given arguments.
** Check first for errors; otherwise an error may interrupt
** the composition of a warning, leaving it unfinished.
*/
int
luaB_warn(lua_State *L);


int
luaB_tonumber(lua_State *L);

int
luaB_getmetatable(lua_State *L);

int
luaB_setmetatable(lua_State *L);

int
luaB_rawequal(lua_State *L);


int
luaB_rawlen(lua_State *L);


int
luaB_rawget(lua_State *L);

int
luaB_rawset(lua_State *L);


int
luaB_collectgarbage(lua_State *L);

int
luaB_type(lua_State *L);

int
luaB_next(lua_State *L);

int
luaB_pairs(lua_State *L);

int
luaB_ipairs(lua_State *L);

int
luaB_select(lua_State *L);

int
luaB_pcall(lua_State *L);

int luaB_xpcall(lua_State *L);

int luaB_tostring(lua_State *L);

#endif // LBASELIB_H
