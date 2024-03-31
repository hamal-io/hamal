import React from "react";
import logoImg from "@/images/template/logo.svg";
import logoLightImg from "@/images/template/logo-light.svg";
import LinkW from "@/types/link.tsx";
import ImageW from "@/types/image.tsx";

export interface LogoProps {
  img?: string;
  imgLight?: string;
  className?: string;
}

const Index: React.FC<LogoProps> = ({
  img = logoImg,
  imgLight = logoLightImg,
  className = "flex-shrink-0",
}) => {
  return (
    <LinkW
      href="/"
      className={`ttnc-logo inline-block text-slate-600 ${className}`}
    >
      {img ? (
        <ImageW
          className={`block h-8 sm:h-10 w-auto ${
            imgLight ? "dark:hidden" : ""
          }`}
          src={img}
          alt="Index"
          sizes="200px"
          // priority
        />
      ) : (
        "logo Here"
      )}
      {imgLight && (
        <ImageW
          className="hidden h-8 sm:h-10 w-auto dark:block"
          src={imgLight}
          alt="Index-Light"
          sizes="200px"
          // priority
        />
      )}
    </LinkW>
  );
};

export default Index;
