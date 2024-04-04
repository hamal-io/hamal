import React, {HTMLProps, MutableRefObject, RefObject} from "react";
import {Position} from "./types";

type DraggableProps = Omit<HTMLProps<HTMLDivElement>, "onDrag" | "onDragEnd"> & {
    objectRef?: RefObject<DOMRect | undefined>;
    onDragDelayStart?: (event: React.MouseEvent | React.TouchEvent) => void;
    onDragStart?: (event: React.MouseEvent | React.TouchEvent) => void;
    onDrag?: (position: Position, event: MouseEvent) => void;
    onDragEnd?: (event: MouseEvent, position: Position) => void;
    onMouseDown?: (event: React.MouseEvent<HTMLDivElement, MouseEvent>) => void;
    onTouchStart?: (event: React.TouchEvent<HTMLDivElement>) => void;
    disabled?: boolean;
    delay?: number;
    innerRef?: MutableRefObject<HTMLDivElement | null>;
    id?: string;
};

export const Draggable = ({
   children,
   objectRef,
   onDragDelayStart,
   onDragStart,
   onDrag,
   onDragEnd,
   onMouseDown,
   onTouchStart,
   disabled,
   delay = 6,
   innerRef,
   ...rest
}: DraggableProps) => {

    const startPosition = React.useRef<Position | null>(null);
    const offset = React.useRef<Position>();
    const wrapper = React.useRef<HTMLDivElement | null>(null);

    const scaledPosition = (e: MouseEvent): Position => {
        const offsetX = offset.current?.x ?? 0;
        const offsetY = offset.current?.y ?? 0;

        const x = e.clientX - (objectRef ? objectRef.current?.left ?? 0 : 0) - offsetX - (objectRef ? objectRef.current?.width ?? 0 : 0) / 2;
        const y = e.clientY - (objectRef ? objectRef.current?.top ?? 0 : 0) - offsetY - (objectRef ? objectRef.current?.height ?? 0 : 0) / 2

        return {x, y};
    };

    const updateposition = (e: MouseEvent) => {
        const position = scaledPosition(e);
        if (onDrag) {
            onDrag(position, e);
        }
    };

    const stopDrag = (e: MouseEvent) => {
        const position = scaledPosition(e);
        if (onDragEnd) {
            onDragEnd(e, position);
        }
        window.removeEventListener("mouseup", stopDrag);
        window.removeEventListener("mousemove", updateposition);
    };

    const startDrag = e => {
        if (onDragStart) {
            onDragStart(e);
        }
        if (wrapper.current && startPosition.current) {
            const nodeRect = wrapper.current.getBoundingClientRect();
            offset.current = {
                x: startPosition.current.x - nodeRect.left,
                y: startPosition.current.y - nodeRect.top
            };
            window.addEventListener("mouseup", stopDrag);
            window.addEventListener("mousemove", updateposition);
        }
    };

    const checkDragDelay = (e: MouseEvent | TouchEvent) => {
        if (startPosition.current) {
            let x: number, y: number;
            if ("ontouchstart" in window && (e as TouchEvent).touches) {
                const touch = (e as TouchEvent).touches[0];
                x = touch.clientX;
                y = touch.clientY;
            } else {
                const mouse = e as MouseEvent;
                e.preventDefault();
                x = mouse.clientX;
                y = mouse.clientY;
            }
            let a = Math.abs(startPosition.current.x - x);
            let b = Math.abs(startPosition.current.y - y);
            let distance = Math.round(Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)));
            let dragDistance = delay;
            if (distance >= dragDistance) {
                startDrag(e);
                endDragDelay();
            }
        }
    };

    const endDragDelay = () => {
        document.removeEventListener("mouseup", endDragDelay);
        document.removeEventListener("mousemove", checkDragDelay);
        startPosition.current = null;
    };

    const startDragDelay = (e: React.MouseEvent | React.TouchEvent) => {
        if (onDragDelayStart) {
            onDragDelayStart(e);
        }
        e.stopPropagation();
        let x: number, y: number;
        if ("ontouchstart" in window && (e as React.TouchEvent).touches) {
            const touch = (e as React.TouchEvent).touches[0];
            x = touch.clientX;
            y = touch.clientY;
        } else {
            e.preventDefault();
            const mouse = e as React.MouseEvent;
            x = mouse.clientX;
            y = mouse.clientY;
        }
        startPosition.current = {x, y};
        document.addEventListener("mouseup", endDragDelay);
        document.addEventListener("mousemove", checkDragDelay);
    };

    return (
        <div
            onMouseDown={e => {
                if (!disabled) {
                    startDragDelay(e);
                }
                if (onMouseDown) {
                    onMouseDown(e);
                }
            }}
            onTouchStart={e => {
                if (!disabled) {
                    startDragDelay(e);
                }
                if (onTouchStart) {
                    onTouchStart(e);
                }
            }}
            onDragStart={e => {
                e.preventDefault();
                e.stopPropagation();
            }}
            ref={ref => {
                wrapper.current = ref;
                if (innerRef) {
                    innerRef.current = ref;
                }
            }}
            {...rest}
        >
            {children}
        </div>
    );
};
