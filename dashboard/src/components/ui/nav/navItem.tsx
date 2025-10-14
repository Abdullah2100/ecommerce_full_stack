import { Label } from "@radix-ui/react-label";
import Image from "next/image";


export interface iNavItemProp {
    name: string,
    icon: string,
    currentIndex: number,
    selectedIndex: number,
    isNewOrder:boolean,
    chageSelectedIndex:(index:number)=>void
}

const NavItem = ({ name, 
    icon, 
    currentIndex, 
    selectedIndex, 
    chageSelectedIndex,
isNewOrder=false }: iNavItemProp) => {
    
    return (
        <div
        onClick={() => chageSelectedIndex(currentIndex)}
        className={`${selectedIndex == currentIndex ? 'bg-[#F0F2F5]' : undefined}
         flex flex-row px-2 py-2 rounded-[3px] relative`}>
            <Image
                src={icon}
                alt={name}
                className="h-6 w-6 text-black me-2 fill-black"
            />
            <Label className="whitespace-nowrap">{name}</Label>
            <div className="w-30" />
            {isNewOrder&&<div className={`${isNewOrder?'bg-red-400':undefined} h-5 w-5 absolute right-2 top-[10px] rounded-full `}/>}

        </div>
    )
}

export default NavItem;
