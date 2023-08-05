import React, {useEffect, useState} from "react";

import {Route} from "react-router-dom";
import Preloader from "./Preloader";
import Sidebar from "./Sidebar";
import Footer from "./Footer";

export const RouteWithSidebar = (props: any) => {
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        const timer = setTimeout(() => setLoaded(true), 1000);
        return () => clearTimeout(timer);
    }, []);

    const localStorageIsSettingsVisible = () => {
        return localStorage.getItem('settingsVisible') !== 'false'
    }

    const [showSettings, setShowSettings] = useState(localStorageIsSettingsVisible);

    const toggleSettings = () => {
        setShowSettings(!showSettings);
        localStorage.setItem('settingsVisible', String(!showSettings));
    }

    return (
        <>
            <Preloader show={!loaded}/>
            <Sidebar/>

            <main className="content">
                {props.component}
                <Footer toggleSettings={toggleSettings} showSettings={showSettings}/>
            </main>
        </>
    )

}

