import React, {useState} from 'react'
import Background from "@/components/background.tsx";
import SectionHero2 from "@/components/template/SectionHero/SectionHero2.tsx";
import SectionHowItWork from "@/components/template/SectionHowItWork/SectionHowItWork.tsx";
import SectionLargeSlider from "@/pages/landing/components/SectionLargeSlider.tsx";
import SectionMagazine8 from "@/components/template/SectionMagazine8.tsx";
import BackgroundSection from "@/components/template/BackgroundSection/BackgroundSection.tsx";
import SectionGridAuthorBox from "@/components/template/SectionGridAuthorBox/SectionGridAuthorBox.tsx";
import SectionSliderCardNftVideo from "@/components/template/SectionSliderCardNftVideo.tsx";
import SectionSliderCollections2 from "@/components/template/SectionSliderCollections2.tsx";
import SectionBecomeAnAuthor from "@/components/template/SectionBecomeAnAuthor/SectionBecomeAnAuthor.tsx";
import SectionGridFeatureNFT2 from "@/pages/landing/components/SectionGridFeatureNFT2.tsx";
import SectionSliderCategories from "@/components/template/SectionSliderCategories/SectionSliderCategories.tsx";
import SectionSubscribe2 from "@/components/template/SectionSubscribe2/SectionSubscribe2.tsx";

const LandingPage: React.FC = () => {
    return (
        <div className="nc-PageHome relative overflow-hidden">
            <Background />

            <div className="container relative mt-5 mb-20 sm:mb-24 lg:mt-20 lg:mb-32">
                <SectionHero2 />

                <SectionHowItWork className="mt-24 lg:mt-40 xl:mt-48" />
            </div>

            <div className="bg-neutral-100/70 dark:bg-black/20 py-20 lg:py-32">
                <div className="container">
                    <SectionLargeSlider />
                </div>
            </div>

            <div className="container relative space-y-24 my-24 lg:space-y-32 lg:my-32">
                <SectionMagazine8 />

                <div className="relative py-20 lg:py-28">
                    <BackgroundSection />
                    <SectionGridAuthorBox
                        sectionStyle="style2"
                        data={Array.from("11111111")}
                        boxCard="box4"
                    />
                </div>

                <SectionSliderCardNftVideo />

                <div className="relative py-20 lg:py-28">
                    <BackgroundSection />
                    <SectionSliderCollections2 cardStyle="style2" />
                </div>

                <SectionBecomeAnAuthor />

                <div className="relative py-20 lg:py-28">
                    <BackgroundSection className="bg-neutral-100/70 dark:bg-black/20 " />
                    <SectionGridFeatureNFT2 />
                </div>

                <SectionSliderCategories />

                <div className="relative py-20 lg:py-24">
                    <BackgroundSection />
                    <SectionSubscribe2 />
                </div>
            </div>
        </div>
    );
}

export default LandingPage;

