import React from "react";
import styles from "./text.module.css";


interface TextArea {
    value?: string;
    placeholder?: string;
    onChange?: (value: string) => void;
}

export const TextArea = ({placeholder, value, onChange}: TextArea) => {

    const handleDragEnd = () => {
        document.removeEventListener("mousemove", handleMouseMove);
        document.removeEventListener("mouseup", handleDragEnd);
    };

    const handleMouseMove = e => {
        e.stopPropagation();
        // updateNodeConnections();
    };

    const handlePossibleResize = e => {
        e.stopPropagation();
        // recalculateStageRect?.();
        document.addEventListener("mousemove", handleMouseMove);
        document.addEventListener("mouseup", handleDragEnd);
    };

    return (
        <div className={styles.wrapper} data-component="text-input">
            <textarea
                data-component="text-input-textarea"
                onChange={e => onChange(e.target.value)}
                onMouseDown={handlePossibleResize}
                placeholder={placeholder}
                className={styles.input}
                value={value}
                onDragStart={e => e.stopPropagation()}
            />
        </div>
    );
};
