import React, {FC, HTMLProps, MutableRefObject, useContext} from "react";
import {Position} from "./types";
import {ContextCanvasRect, ContextCanvasState} from "@/components/nodes/context.ts";

type DraggableProps = Omit<HTMLProps<HTMLDivElement>, "onDrag" | "onDragEnd"> & {
    id?: string;

    onDragDelayStart?: (event: React.MouseEvent | React.TouchEvent) => void;
    onDragStart?: (event: React.MouseEvent | React.TouchEvent) => void;
    onDrag?: (position: Position, event: MouseEvent) => void;
    onDragEnd?: (event: MouseEvent, position: Position) => void;
    onMouseDown?: (event: React.MouseEvent<HTMLDivElement, MouseEvent>) => void;
    onTouchStart?: (event: React.TouchEvent<HTMLDivElement>) => void;

    disabled?: boolean;
    delay?: number;
    innerRef?: MutableRefObject<HTMLDivElement | null>;
};

export const Draggable: FC<DraggableProps> = ({
      children,
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
  }) => {
    const canvasState = useContext(ContextCanvasState)
    const canvasRect = useContext(ContextCanvasRect)

    const startPosition = React.useRef<Position | null>(null);
    const offset = React.useRef<Position>();
    const wrapper = React.useRef<HTMLDivElement | null>(null);

    const byScale = (value: number) => (1 / canvasState.scale) * value;

    const getScaledPosition = (e: MouseEvent): Position => {
        const offsetX = offset.current?.x ?? 0;
        const offsetY = offset.current?.y ?? 0;

        const x =
            byScale(
                e.clientX - (canvasRect ? canvasRect.current?.left ?? 0 : 0) - offsetX - (canvasRect ? canvasRect.current?.width ?? 0 : 0) / 2
            ) + byScale(canvasState.position.x);

        const y = byScale(
            e.clientY - (canvasRect ? canvasRect.current?.top ?? 0 : 0) - offsetY - (canvasRect ? canvasRect.current?.height ?? 0 : 0) / 2
        ) + byScale(canvasState.position.y);


        return {x, y};
    };

    const updatePosition = (e: MouseEvent) => {
        const position = getScaledPosition(e);
        if (onDrag) {
            onDrag(position, e);
        }
    };

    const stopDrag = (e: MouseEvent) => {
        const position = getScaledPosition(e);
        if (onDragEnd) {
            onDragEnd(e, position);
        }
        window.removeEventListener("mouseup", stopDrag);
        window.removeEventListener("mousemove", updatePosition);
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
            window.addEventListener("mousemove", updatePosition);
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