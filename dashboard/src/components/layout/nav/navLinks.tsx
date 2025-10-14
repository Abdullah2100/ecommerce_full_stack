import { useState } from "react";
import homIcon from "../../../../public/images/home.svg";
import logo from "../../../../public/images/logo.svg";
import myInfo from "../../../../public/images/user.svg";
import varient from "../../../../public/images/products-major.svg";
import users from "../../../../public/images/users.svg";
import store from "../../../../public/images/store.svg";
import category from "../../../../public/images/category.svg";
import order from "../../../../public/images/order.svg";

import product from "../../../../public/images/product.svg";
import Image from "next/image";
import NavItem, { iNavItemProp } from "@/components/ui/nav/navItem";
import useOrder from "@/stores/order";
import { changeStoreStatus } from "@/lib/api/store";

export interface iNavProp {
  selectedIndex: number;
  setSelectedIndex: (index: number) => void;
  isNewOrder: boolean;
}

const NavLink = ({ selectedIndex, setSelectedIndex, isNewOrder }: iNavProp) => {
  const { getOrdersAt,changeHasNewOrderStatus,getOrderStatus } = useOrder();

  const navLinkItems: iNavItemProp[] = [
    {
      name: "Home",
      icon: homIcon,
      currentIndex: 0,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex,
    },
    {
      name: "My Info",
      icon: myInfo,
      currentIndex: 1,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex,
    },
    {
      name: "Varient",
      icon: varient,
      currentIndex: 2,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex,
    },

    {
      name: "Category",
      icon: category,
      currentIndex: 3,
      selectedIndex: selectedIndex,
      isNewOrder: false,

      chageSelectedIndex: setSelectedIndex,
    },

    {
      name: "Users",
      icon: users,
      currentIndex: 4,
      selectedIndex: selectedIndex,
      isNewOrder: false,

      chageSelectedIndex: setSelectedIndex,
    },
    {
      name: "Stores",
      icon: store,
      currentIndex: 5,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex,
    },

    {
      name: "Product",
      icon: product,
      currentIndex: 6,
      selectedIndex: selectedIndex,
      isNewOrder: false,

      chageSelectedIndex: setSelectedIndex,
    },

    {
      name: "Orders",
      icon: order,
      currentIndex: 7,
      isNewOrder: isNewOrder,
      selectedIndex: selectedIndex,
      chageSelectedIndex: (index) => {
        setSelectedIndex(index);
        getOrdersAt(1);
        getOrderStatus();
        changeHasNewOrderStatus(false)
      },
    },
  ];

  return (
    <div className="sticky top-0">
      <Image className={`h-24 w-30 object-contain`} src={logo} alt={"logo"} />
      {navLinkItems.map((item, index) => (
        <NavItem key={index} {...item} />
      ))}
    </div>
  );
};

export default NavLink;
