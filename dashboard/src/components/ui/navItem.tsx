import Image from "next/image";
import { Label } from "./label";
import { SetStateAction } from "react";


export interface iNavItemProp {
    name: string,
    icon: string,
    currentIndex: number,
    selectedIndex: number,
    chageSelectedIndex:(index:number)=>void
}

const NavItem = ({ name, icon, currentIndex, selectedIndex, chageSelectedIndex }: iNavItemProp) => {

    return (
        <div
        onClick={() => chageSelectedIndex(currentIndex)}
        className={`${selectedIndex == currentIndex ? 'bg-[#F0F2F5]' : undefined}
         flex flex-row px-2 py-2 rounded-[3px]`}>
            <Image
                src={icon}
                alt={name}
                className="h-6 w-6 text-black me-2 fill-black"
            />
            <Label className="whitespace-nowrap">{name}</Label>
            <div className="w-30" />

        </div>
    )
}

export default NavItem;
