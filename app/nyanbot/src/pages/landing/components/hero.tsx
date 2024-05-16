import React, {FC} from "react";
import ImageW from "@/types/image.tsx";
import imagePng from "@/images/hero.png";
import {MetaMaskButton} from "@/app/metamask.tsx";

export interface HeroProps {
    children?: React.ReactNode;
    className?: string;
}

export const Hero: FC<HeroProps> = ({className = "", children}) => {
    return (
        <div className={`nc-SectionHero3 relative`}>
            <div className="mt-10 lg:mt-0 lg:absolute lg:container z-10 inset-x-0 top-[10%] sm:top-[20%]">
                <div className="flex flex-col items-start max-w-2xl space-y-5 xl:space-y-8 ">
          <span className="sm:text-lg md:text-xl font-semibold text-neutral-900">
            Explore
          </span>
                    <h2 className="font-bold text-black text-3xl sm:text-4xl md:text-5xl lg:text-6xl xl:text-7xl !leading-[115%] ">
                        Title
                    </h2>
                    <MetaMaskButton
                        className="px-6 py-3 lg:px-8 lg:py-4 text-sm sm:text-base lg:text-lg font-medium"
                    />
                </div>
            </div>
            <div className="relative aspect-w-4 aspect-h-3 sm:aspect-w-16 sm:aspect-h-9">
                <ImageW
                    className="absolute inset-0 object-cover rounded-[32px]"
                    fill={true}
                    src={imagePng}
                    alt="hero"
                />
            </div>
        </div>
    )

};

