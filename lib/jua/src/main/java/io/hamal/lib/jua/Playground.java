package io.hamal.lib.jua;

import org.terasology.jua.LuaState53;

public class Playground {

    public static void main(String[] args) {
        System.load("/home/ddymke/Repo/hamal/lib/jua/native/jua/build/libjua.so");
        var s = new LuaState53();

        System.out.println(s.lua_versionnum());
    }

}
