#include "kua_common.h"
#include "kua_memory.h"
#include "kua_memory_arena.h"

static Arena default_arena = {0};

void *
memory_arena_allocate(size_t bytes) {
    return arena_alloc(&default_arena, bytes);
}

void *
memory_arena_reallocate(void *L, void *ptr, size_t old_size, size_t new_size) {
    return arena_realloc(&default_arena, ptr, old_size, new_size);
}

void
memory_arena_reset(void) {
    arena_reset(&default_arena);
}

void
memory_arena_free(void) {
    arena_free(&default_arena);
}
