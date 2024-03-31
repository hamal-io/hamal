import React, { FC } from "react";
import { ArrowRightIcon } from "@heroicons/react/24/solid";
import Index from "@/components/ui/avatar";
import NcImage from "@/components/ui/image/NcImage";
import VerifyIcon from "@/components/template/VerifyIcon";
import useGetRandomData from "@/hooks/template/useGetRandomData.ts";
import LinkW from "@/types/link.tsx";

export interface CardAuthorBox2Props {
  className?: string;
}

const CardAuthorBox2: FC<CardAuthorBox2Props> = ({ className = "" }) => {
  const { personNameRd, nftImageRd } = useGetRandomData();

  return (
    <LinkW
      href={"/author"}
      className={`nc-CardAuthorBox2 flex flex-col overflow-hidden [ nc-box-has-hover nc-dark-box-bg-has-hover ] ${className}`}
    >
      <div className="relative flex-shrink-0 ">
        <div>
          <NcImage
            fill={true}
            containerClassName="flex aspect-w-7 aspect-h-5 sm:aspect-h-6 w-full h-0"
            src={nftImageRd}
          />
        </div>
        <div className="absolute top-3 inset-x-3 flex">
          <div className=" py-1 px-4 bg-neutral-100 dark:bg-neutral-800 rounded-full flex items-center justify-center leading-none text-xs font-medium">
            1.45 ETH <ArrowRightIcon className="w-5 h-5 text-yellow-600 ml-3" />
          </div>
        </div>
      </div>

      <div className="-mt-8 m-8 text-center">
        <Index
          containerClassName="ring-2 ring-white"
          sizeClass="w-16 h-16 text-2xl"
          radius="rounded-full"
        />
        <div className="mt-3">
          <h2
            className={`text-base font-medium flex items-center justify-center`}
          >
            {personNameRd}
            <VerifyIcon />
          </h2>
          <span
            className={`block mt-1 text-sm text-neutral-500 dark:text-neutral-400`}
          >
            @creator
          </span>
        </div>
      </div>
    </LinkW>
  );
};

export default CardAuthorBox2;
