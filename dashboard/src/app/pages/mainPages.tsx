'use client';

import MyInfoPage from "./myinfoPage"
import VarientPage from "./varientPage"
import UsersPage from "./usersPage"
import StoresPage from "./storesPage"
import CategoryPage from "./CategoryPage"
import ProductPage from "./productPage"
import OrderPage from "./orderPage"

interface iMainPageProp {
    currentPage: number
}

const MainPage = ({ currentPage }: iMainPageProp) => {


    return (<div className="py-10 px-5  w-auto" >
        {(() => {
            switch (currentPage) {
                case 1:
                    return <MyInfoPage />;
                case 2:
                    return <VarientPage />
                case 3:
                    return <CategoryPage />
                case 4:
                    return <UsersPage />
                case 5:
                    return <StoresPage />
                case 6:
                    return <ProductPage />
                case 7:
                    return <OrderPage/>
                default:
                    return null;
            }
        })()}
    </div>
    )
}
export default MainPage