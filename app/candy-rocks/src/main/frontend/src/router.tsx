import React from "react";
import {createBrowserRouter} from "react-router-dom";

// public
import LandingPage from "./pages/landing";

export const router = createBrowserRouter([
    {path: "/", element: <LandingPage/>},
]);
