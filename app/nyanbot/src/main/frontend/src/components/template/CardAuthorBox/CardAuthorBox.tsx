import FollowButton from "@/components/template/FollowButton";
import VerifyIcon from "@/components/template/VerifyIcon";
import React, { FC } from "react";
import Index from "@/components/ui/avatar";
import Badge from "@/components/ui/badge/Badge";
import useGetRandomData from "@/hooks/template/useGetRandomData.ts";
import LinkW from "@/types/link.tsx";

export interface CardAuthorBoxProps {
  className?: string;
  index?: number;
}

const CardAuthorBox: FC<CardAuthorBoxProps> = ({ className = "", index }) => {
  const { personNameRd } = useGetRandomData();

  return (
    <LinkW
      href={"/author"}
      className={`nc-CardAuthorBox relative flex flex-col items-center justify-center text-center px-3 py-5 sm:px-6 sm:py-7  [ nc-box-has-hover ] [ nc-dark-box-bg-has-hover ] ${className}`}
    >
      {index && (
        <Badge
          className="absolute left-3 top-3"
          color={index === 1 ? "red" : index === 2 ? "blue" : "green"}
          name={`#${index}`}
        />
      )}
      <Index sizeClass="w-20 h-20 text-2xl" radius="rounded-full" />
      <div className="mt-3">
        <h2 className={`text-base font-semibold flex items-center`}>
          {personNameRd}
          <VerifyIcon />
        </h2>
        <div className={`mt-1 text-sm font-medium`}>
          <span>1.549</span>
          {` `}
          <span className="text-neutral-400 font-normal">ETH</span>
        </div>
      </div>
      <FollowButton className="mt-3" />
    </LinkW>
  );
};

export default CardAuthorBox;
