"use client";

import React, { useMemo } from "react";
import HeaderLogged from "@/components/template/Header/Header2.tsx";
import Header2 from "@/components/template/Header/Header2";
import usePathnameW from "@/hooks/template/usePathname.ts";
import {useLocation, useParams} from "react-router-dom";

const SiteHeader = () => {
  const location = useLocation();



  return (<HeaderLogged/>);
};

export default SiteHeader;
