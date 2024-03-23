import React from "react";

const ImageW = ({src, className = "", alt, fill = false , ...props}) => {
    //fill is nextjs, stretches img to parents dimension, drop it for now
    return (
        <img src={src} className={className} alt={alt}{...props}>
            {props.children}
        </img>
    );
};

export default ImageW;