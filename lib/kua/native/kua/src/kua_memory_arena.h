#ifndef KUA_MEMORY_ARENA_H
#define KUA_MEMORY_ARENA_H

#include <stddef.h>
#include <stdint.h>
#include <assert.h>


typedef struct Region Region;

struct Region {
    Region *next;
    size_t count;
    size_t capacity;
    uintptr_t data[];
};

typedef struct {
    Region *begin, *end;
} Arena;

//#define REGION_DEFAULT_CAPACITY (8*1024)
#define REGION_DEFAULT_CAPACITY (64)

Region *new_region(size_t capacity);

void free_region(Region *r);

// TODO: snapshot/rewind capability for the default_arena
// - Snapshot should be combination of a->end and a->end->count.
// - Rewinding should be restoring a->end and a->end->count from the snapshot and
// setting count-s of all the Region-s after the remembered a->end to 0.
void *arena_alloc(Arena *a, size_t size_bytes);

void *arena_realloc(Arena *a, void *oldptr, size_t oldsz, size_t newsz);

void arena_reset(Arena *a);

void arena_free(Arena *a);

#endif //KUA_MEMORY_ARENA_H
