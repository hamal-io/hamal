#ifndef KUA_MEMORY_H
#define KUA_MEMORY_H

void
memory_arena_init(size_t max_size);

void *
memory_arena_allocate(size_t bytes);

void *
memory_arena_reallocate(void *L, void *ptr, size_t old_size, size_t new_size);

void
memory_arena_reset(void);

void
memory_arena_free(void);

#endif //KUA_MEMORY_H
