'use client';

import MyInfoPage from "./myinfo"
import Variant from "./varient"
import Users from "./users"
import Stores from "./stores"
import Category from "./category"
import Product from "./product"
import Order from "./order"

interface iMainPageProp {
    currentPage: number
}

const Main = ({ currentPage }: iMainPageProp) => {


    return (<div className="py-10 px-5  w-auto" >
        {(() => {
            switch (currentPage) {
                case 1:
                    return <MyInfoPage />;
                case 2:
                    return <Variant />
                case 3:
                    return <Category />
                case 4:
                    return <Users />
                case 5:
                    return <Stores />
                case 6:
                    return <Product />
                case 7:
                    return <Order/>
                default:
                    return null;
            }
        })()}
    </div>
    )
}
export default Main