"use client";

import React, { FC, useEffect, useState } from "react";
import Avatar from "@/components/shared/Avatar/Avatar";
import NcImage from "@/components/shared/NcImage/NcImage";
import ItemTypeImageIcon from "./ItemTypeImageIcon";
import LikeButton from "./LikeButton";
import ItemTypeVideoIcon from "./ItemTypeVideoIcon";
import Prices from "./Prices";
import RemainingTimeNftCard from "./RemainingTimeNftCard";
import useGetRandomData from "@/hooks/useGetRandomData";
import LinkW from "@/types/link.tsx";

export interface CardNFT2Props {
  className?: string;
  isLiked?: boolean;
}

const CardNFT2: FC<CardNFT2Props> = ({ className = "", isLiked }) => {
  const { titleRd, nftImageRd } = useGetRandomData();

  const [itemType, setItemType] = useState<"video" | "audio" | "default">(
    "default"
  );

  useEffect(() => {
    if (Math.random() > 0.5) {
      setItemType("video");
    } else {
      setItemType("audio");
    }
  }, []);

  const renderAvatars = () => {
    return (
      <div className="hidden md:flex -space-x-1.5 ">
        <Avatar
          containerClassName="ring-2 ring-white "
          sizeClass="h-5 w-5 text-sm"
        />
        <Avatar
          containerClassName="ring-2 ring-white "
          sizeClass="h-5 w-5 text-sm"
        />
        <Avatar
          containerClassName="ring-2 ring-white "
          sizeClass="h-5 w-5 text-sm"
        />
      </div>
    );
  };

  return (
    <div
      className={`nc-CardNFT2 relative bg-white dark:bg-neutral-900 rounded-3xl flex flex-col group p-2.5  ${className}`}
      data-nc-id="CardNFT2"
    >
      <div className="relative flex-shrink-0 ">
        <div>
          <NcImage
            containerClassName="flex relative aspect-w-11 aspect-h-12 w-full h-0 rounded-3xl overflow-hidden z-0"
            src={nftImageRd}
            className="object-cover group-hover:scale-[1.03] transition-transform duration-300 ease-in-out will-change-transform"
            fill
          />
        </div>

        {itemType === "video" && (
          <ItemTypeVideoIcon className="absolute bottom-2.5 right-2.5 w-7 h-7 md:w-9 md:h-9" />
        )}
        {itemType === "audio" && (
          <ItemTypeImageIcon className="absolute bottom-2.5 right-2.5 w-7 h-7 md:w-9 md:h-9" />
        )}

        {/* LIKE AND AVATARS */}
        <div className="absolute top-2.5 left-2.5 z-10 flex items-center space-x-2">
          <LikeButton liked={isLiked} className=" !h-9" />
          {renderAvatars()}
        </div>

        {/* ----TIME--- */}
        <RemainingTimeNftCard contentClassName="right-5 top-1/2 -translate-y-1/2 pb-1" />

        <div className="absolute left-[-1px] bottom-[-0.4px] ">
          <svg
            className="text-white dark:text-neutral-900 w-64 md:w-[281px]"
            width="281"
            viewBox="0 0 281 99"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M0 0V99H258.059C248.54 99 239.92 93.3743 236.089 84.6606L205.167 14.3394C201.335 5.62568 192.716 0 183.197 0H0Z"
              fill="currentColor"
            />
          </svg>

          <div className="absolute left-4 bottom-0 w-48 ">
            <h2 className={`text-lg font-semibold `}>{titleRd}</h2>

            <div className="w-full mt-1.5 flex justify-between items-end ">
              <Prices labelTextClassName="bg-white dark:bg-neutral-900 " />
              <span className="block text-neutral-500 dark:text-neutral-400 text-xs">
                11 in stock
              </span>
            </div>
          </div>
        </div>
      </div>

      <LinkW href={"/nft-detail"} className="absolute inset-0"></LinkW>
    </div>
  );
};

export default CardNFT2;