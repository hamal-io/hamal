"use client";

import React, { useMemo } from "react";
import HeaderLogged from "@/components/Header/HeaderLogged";
import Header2 from "@/components/Header/Header2";
import usePathnameW from "@/hooks/usePathname.ts";

const SiteHeader = () => {
  let pathname = usePathnameW();

  const headerComponent = useMemo(() => {
    let HeadComponent = HeaderLogged;

    switch (pathname) {
      case "/home-3":
        HeadComponent = Header2;
        break;

      default:
        break;
    }

    return <HeadComponent />;
  }, [pathname]);

  return <>{headerComponent}</>;
};

export default SiteHeader;
