import React from 'react'
import {BackgroundGlass} from "@/components/background.tsx";
import {Hero} from "@/pages/landing/components/hero.tsx";
import {HowItWorks} from "@/pages/landing/components/how-it-works.tsx";

const LandingPage: React.FC = () => {
    return (
        <div className="relative overflow-hidden h-screen">
            <BackgroundGlass/>

            <div className="container relative mt-5 mb-20 sm:mb-24 lg:mt-20 lg:mb-32">
                <Hero/>

                <HowItWorks className="mt-24 lg:mt-40 xl:mt-48"/>
            </div>

        </div>
    );
}

export default LandingPage;

