import React from 'react';
export default (props: any) => {

    const {show} = props;

    return (
        <div
            className={`preloader bg-soft flex-column justify-content-center align-items-center ${show ? "" : "show"}`}>
            {/*<Image className="loader-element animate__animated animate__jackInTheBox" src={Logo} height={40} />*/}
        </div>
    );
};
