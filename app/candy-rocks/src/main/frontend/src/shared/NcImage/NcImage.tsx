import React, { FC } from "react";
import ImageW from "@/types/image.tsx";
/*
export interface NcImageProps extends Omit<ImageProps, "alt"> {
  containerClassName?: string;
  alt?: string;
}
*/

interface NcImageProps {
  containerClassName?: string;
  alt?: string;
  src?: string;
  fill?: boolean;
  className?: string;
  sizes?: string;
}


const NcImage: FC<NcImageProps> = ({
  containerClassName = "relative",
  alt = "nc-imgs",
  src,
  fill,
  className = "object-cover w-full h-full",
  sizes = "(max-width: 600px) 480px, 800px",
  ...args
}) => {
  return (
    <div className={containerClassName}>
      {src ? (
        <ImageW
          className={className}
          alt={alt}
          sizes={sizes}
          {...args}
          src={src}
          fill={fill}
        />
      ) : null}
    </div>
  );
};

export default NcImage;
