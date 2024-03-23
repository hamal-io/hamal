import React from "react";
import logoImg from "@/images/logo.svg";
import logoLightImg from "@/images/logo-light.svg";
import LinkW from "@/types/link.tsx";
import ImageW from "@/types/image.tsx";

export interface LogoProps {
  img?: string;
  imgLight?: string;
  className?: string;
}

const Logo: React.FC<LogoProps> = ({
  img = logoImg,
  imgLight = logoLightImg,
  className = "flex-shrink-0",
}) => {
  return (
    <LinkW
      href="/"
      className={`ttnc-logo inline-block text-slate-600 ${className}`}
    >
      {/* THIS USE FOR MY CLIENT */}
      {/* PLEASE UN COMMENT BELLOW CODE AND USE IT */}
      {img ? (
        <ImageW
          className={`block h-8 sm:h-10 w-auto ${
            imgLight ? "dark:hidden" : ""
          }`}
          src={img}
          alt="Logo"
          sizes="200px"
          // priority
        />
      ) : (
        "Logo Here"
      )}
      {imgLight && (
        <ImageW
          className="hidden h-8 sm:h-10 w-auto dark:block"
          src={imgLight}
          alt="Logo-Light"
          sizes="200px"
          // priority
        />
      )}
    </LinkW>
  );
};

export default Logo;
