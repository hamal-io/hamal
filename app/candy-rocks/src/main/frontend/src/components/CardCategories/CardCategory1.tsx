import { _getImgRd, _getTagNameRd } from "@/contains/fakeData";
import React, { FC } from "react";
import NcImage from "@/shared/NcImage/NcImage";
import LinkW from "@/types/link.tsx";

export interface CardCategory1Props {
  className?: string;
  size?: "large" | "normal";
  featuredImage?: string;
  name?: string;
  desc?: string;
}

const CardCategory1: FC<CardCategory1Props> = ({
  className = "",
  size = "normal",
  name = "",
  desc = "",
  featuredImage = "",
}) => {
  return (
    <LinkW
      href={"/collection"}
      className={`nc-CardCategory1 flex items-center ${className}`}
    >
      <NcImage
        alt=""
        containerClassName={`flex-shrink-0 relative ${
          size === "large" ? "w-20 h-20" : "w-12 h-12"
        } rounded-lg mr-4 overflow-hidden`}
        src={featuredImage || _getImgRd()}
        sizes="(max-width: 640px) 100vw, 40vw"
        fill
      />
      <div>
        <h2
          className={`${
            size === "large" ? "text-lg" : "text-base"
          } nc-card-title text-neutral-900 dark:text-neutral-100 font-semibold`}
        >
          {name || _getTagNameRd()}
        </h2>
        <span
          className={`${
            size === "large" ? "text-sm" : "text-xs"
          } block mt-[2px] text-neutral-500 dark:text-neutral-400`}
        >
          {desc}
        </span>
      </div>
    </LinkW>
  );
};

export default CardCategory1;
