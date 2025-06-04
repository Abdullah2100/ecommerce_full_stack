import { Label } from "@/components/ui/label"
import MyInfoPage from "./myinfoPage"

interface iMainPageProp{
    currentPage:number
}

const MainPage = ({currentPage}:iMainPageProp)=>{


    return (<div className="py-10 px-5  w-[250px]" >
        {(() => {
            switch (currentPage) {
                case 1:
                    return <MyInfoPage />;
                default:
                    return null;
            }
        })()}
    </div>
    )
}
export default MainPage