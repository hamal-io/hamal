import { useLocation } from 'react-router-dom';

//Wrapper for next.js
const usePathnameW = () => {
    const location = useLocation();
    return location.pathname;
};

export default usePathnameW;