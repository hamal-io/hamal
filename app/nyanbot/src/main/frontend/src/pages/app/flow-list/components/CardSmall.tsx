import {forwardRef} from "react";
import LinkW from "@/types/link.tsx";

export const CardSmall = () => {
    return (
        <div
            className={`relative flex justify-between p-2 space-x-2 rounded-3xl bg-neutral-100 dark:bg-neutral-800 hover:shadow-xl transition-shadow `}
        >
            <LinkW href={""} className="flex-grow flex space-x-4">
                <div className="relative w-16 sm:w-24">
                    Akk
                    {/*<NcImage
                        containerClassName="absolute inset-0 rounded-2xl overflow-hidden shadow-lg z-0"
                        src={featuredImage}
                        fill
                    />*/}
                </div>

                <div className="flex flex-col justify-center flex-grow">
                    <h2 className={`block font-medium sm:text-lg`}>NFT music #114</h2>
                    <div className=" flex items-center pt-3 mt-1.5">

                        {/*<Prices
                            price="1.00 ETH"
                            labelText="Price"
                            className="sm:ml-3.5"
                            contentClass="py-1.5 px-2 sm:px-3 text-xs sm:text-sm font-semibold"
                            labelTextClassName="bg-neutral-100 dark:bg-neutral-800 "
                        />*/}
                        <span className="block ml-3.5 text-neutral-500 dark:text-neutral-400 text-xs">
              1 of 100
            </span>
                    </div>
                </div>
            </LinkW>


        </div>
    )
}