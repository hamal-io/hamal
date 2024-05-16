import React from "react";

const LinkW  = ({href, className = "", ...props}) => {
    return (
        <a href={href} className={className} {...props}>
            {props.children}
        </a>
    );
};

export default LinkW;