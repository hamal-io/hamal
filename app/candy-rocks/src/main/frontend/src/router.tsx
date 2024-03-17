import React from "react";
import {createBrowserRouter} from "react-router-dom";

// public
import HomePage from "./pages/landing";

export const router = createBrowserRouter([
    {path: "/", element: <HomePage/>},
]);
