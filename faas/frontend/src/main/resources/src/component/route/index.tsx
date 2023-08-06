import React, {useEffect, useState} from "react";
import Preloader from "../preloader";
import Sidebar from "../sidebar";
import Footer from "../footer";

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

