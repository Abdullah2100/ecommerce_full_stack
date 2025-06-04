"use client";
import NavLink from "@/components/ui/navLinks";
import { useEffect, useState } from "react";
import MainPage from "../pages/mainPages";
import isAuth from "../middleware/routProtection";

const Home = () => {
    const [selectedIndex, setSelectedIndex] = useState(0);

    useEffect(()=>{
        console.log(`the selecte index is ${selectedIndex}`)
    },[selectedIndex])
    return (
        <div className="flex flex-row px-10">
            <div className="w-auto">
                <NavLink selectedIndex={selectedIndex} setSelectedIndex={setSelectedIndex} />
            </div>
            <div className="flex-grow overflow-y-auto">
                <MainPage currentPage={selectedIndex} />
            </div>
        </div>
    )
}

export default isAuth(Home);