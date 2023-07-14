package io.hamal.lib.kua;

import org.terasology.kua.LuaState53;

public class Playground {

    public static void main(String[] args) {
        System.load("/home/ddymke/Repo/hamal/lib/kua/native/kua/build/libkua.so");
        var s = new LuaState53();

        System.out.println(s.lua_versionnum());
    }

}
