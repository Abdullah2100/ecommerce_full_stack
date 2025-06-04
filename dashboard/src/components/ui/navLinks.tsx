import { useState } from "react";
import NavItem, { iNavItemProp } from "./navItem"
import homIcon from '../../../public/home.svg'
import logo from '../../../public/logo.svg'
import myInfo from '../../../public/user.svg'
import Image from 'next/image'

export interface iNavProp {
    selectedIndex: number
    setSelectedIndex: (index: number) => void

}
const NavLink = ({selectedIndex,setSelectedIndex}:iNavProp) => {
    const navLinkItems: iNavItemProp[] = [
        {
            name: "Home",
            icon: homIcon,
            currentIndex: 0,
            selectedIndex: selectedIndex,
            chageSelectedIndex: setSelectedIndex,
        },
        {
            name: "My Info",
            icon: myInfo,
            currentIndex: 1,
            selectedIndex: selectedIndex,
            chageSelectedIndex: setSelectedIndex
        },
    ]

    return (<div className="sticky top-0">
        <Image
            className='h-24 w-30 object-contain'
            src={logo} alt={'logo'} />
        {navLinkItems.map((item, index) => (
            <NavItem key={index} {...item} />
        ))}
    </div>)
}

export default NavLink