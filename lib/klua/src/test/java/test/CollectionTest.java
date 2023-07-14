/*
 * Copyright (C) 2008,2012 Andre Naef
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package test;


import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Contains unit tests for collections backed by Lua tables.
 */
public class CollectionTest extends AbstractLuaTest {
	// -- Test cases
	/**
	 * Tests the map.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testMap() throws Exception {
		// Get a map backed by Lua
		luaState.newTable();
		Map<Object, Object> map = luaState.toJavaObject(-1, Map.class);

		// isEmpty(), size()
		assertTrue(map.isEmpty());
		assertEquals(0, map.size());

		// put()
		map.put("t", "test");
		assertFalse(map.isEmpty());
		assertEquals(1, map.size());
		luaState.getField(-1, "t");
		assertEquals("test", luaState.toString(-1));
		luaState.pop(1);

		// containsKey()
		assertTrue(map.containsKey("t"));

		// containsValue()
		assertTrue(map.containsValue("test"));

		// get()
		assertEquals("test", map.get("t"));

		// putAll()
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("v", "test2");
		map.putAll(map2);
		assertEquals("test2", map.get("v"));
		luaState.getField(-1, "v");
		assertEquals("test2", luaState.toString(-1));
		luaState.pop(1);

		// remove()
		map.remove("v");
		assertNull(map.get("v"));
		luaState.getField(-1, "v");
		assertTrue(luaState.isNil(-1));
		luaState.pop(1);

		// entrySet()
		int count = 0;
		for (Map.Entry<Object, Object> entry : map.entrySet()) {
			if (entry.getKey() != null) {
				count++;
			}
		}
		assertEquals(map.size(), count);

		// values()
		boolean found = false;
		for (Object object : map.values()) {
			if (object.equals("test")) {
				found = true;
				break;
			}
		}
		assertTrue(found);

		// keySet()
		assertTrue(map.keySet().contains("t"));
		Iterator<Object> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().equals("t")) {
				iterator.remove();
			}
		}
		assertFalse(map.containsKey("t"));

		// clear()
		map.clear();
		assertEquals(0, map.size());
		assertTrue(map.isEmpty());

		// Finish
		luaState.pop(1);
		assertEquals(0, luaState.getTop());
	}

	/**
	 * Tests the list.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testList() throws Exception {
		// Get a list backed by Lua
		luaState.newTable();
		List<Object> list = luaState.toJavaObject(-1, List.class);

		// isEmpty(), size()
		assertTrue(list.isEmpty());
		assertEquals(0, list.size());

		// add()
		list.add("test");
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		luaState.rawGet(-1, 1);
		assertEquals("test", luaState.toString(-1));
		luaState.pop(1);

		// contains()
		assertTrue(list.contains("test"));

		// get()
		assertEquals("test", list.get(0));

		// addAll()
		List<Object> list2 = new ArrayList<Object>();
		list2.add("test2");
		list.addAll(0, list2);
		assertEquals("test2", list.get(0));
		luaState.rawGet(-1, 1);
		assertEquals("test2", luaState.toString(-1));
		luaState.pop(1);

		// remove()
		list.remove(0);
		assertEquals(1, list.size());
		assertEquals(1, luaState.rawLen(-1));
		luaState.rawGet(-1, 1);
		assertEquals("test", luaState.toString(-1));
		luaState.pop(1);

		// iterator()
		int count = 0;
		for (Object object : list) {
			assertEquals("test", object);
			count++;
		}
		assertEquals(list.size(), count);

		// clear()
		list.clear();
		assertEquals(0, list.size());
		assertTrue(list.isEmpty());

		// Finish
		luaState.pop(1);
		assertEquals(0, luaState.getTop());
	}
}
