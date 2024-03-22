import React from "react";

const LinkW  = ({href, className = "", ...props}) => {
    return (
        <link href={href} className={className} {...props}>
            {props.children}
        </link>
    );
};

export default LinkW;