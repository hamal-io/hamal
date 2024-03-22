import React from "react";

const ImageW  = ({src, className="", alt ,...props}) => {
    return (
        <img src={src} className={className} alt={alt} {...props}>
            {props.children}
        </img>
    );
};

export default ImageW;