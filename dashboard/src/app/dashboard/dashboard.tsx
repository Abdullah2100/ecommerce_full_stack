"use client";

import NavLink from "@/components/layout/nav/navLinks";
import { useEffect, useState } from "react";
import Main from "./body/main";

interface DashboardContentProps {}

const Dashboard = ({}: DashboardContentProps) => {
    const [selectedIndex, setSelectedIndex] = useState(0);

    useEffect(() => {
        console.log(`the selected index is ${selectedIndex}`);
    }, [selectedIndex]);

    return (
        <div className="flex flex-row px-10">
            <div className="w-auto ">
                <NavLink selectedIndex={selectedIndex} setSelectedIndex={setSelectedIndex} />
            </div>
            <div className="flex-grow overflow-y-auto">
                <Main currentPage={selectedIndex} />
            </div>
        </div>
    );
};

export default Dashboard;
