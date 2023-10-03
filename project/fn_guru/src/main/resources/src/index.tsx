import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import Theme from "./theme.tsx";
import {Flowbite} from 'flowbite-react'
import HomePage from "./landing/page/home";

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <Flowbite theme={{theme: Theme}}>
            <HomePage/>
        </Flowbite>
    </React.StrictMode>,
)

