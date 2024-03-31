import React, {FC} from "react";

export const BgGlassmorphism = ({
  className = "absolute inset-x-0 md:top-10 min-h-0 pl-20 py-24 flex overflow-hidden z-0",
}) => {
  return (
    <div className={`nc-BgGlassmorphism ${className}`}>
      <span className="block bg-[#ef233c] w-72 h-72 rounded-full mix-blend-multiply filter blur-3xl opacity-10 lg:w-96 lg:h-96"></span>
      <span className="block bg-[#04868b] w-72 h-72 -ml-20 mt-40 rounded-full mix-blend-multiply filter blur-3xl opacity-10 lg:w-96 lg:h-96 nc-animation-delay-2000"></span>
    </div>
  );
};

export const Background = ({children}) => {
    return (
        <div className="bg-white text-base dark:bg-neutral-900 text-neutral-900 dark:text-neutral-200">
            {children}
        </div>
    )
}