import React from "react";
import HeaderLogged from "@/components/template/Header/Header2.tsx";
import {useLocation} from "react-router-dom";

const SiteHeader = () => {
    const location = useLocation();
    return (<HeaderLogged/>);
};

export default SiteHeader;
