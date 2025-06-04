import { Label } from "@/components/ui/label"
import MyInfoPage from "./myinfoPage"
import VarientPage from "./varientPage"

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
                default:
                    return null;
            }
        })()}
    </div>
    )
}
export default MainPage