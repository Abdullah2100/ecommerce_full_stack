"use client"

import Image from "next/image";
import Util from "./util/globle";
import { useRouter } from "next/navigation";
import isAuth from "./middleware/routProtection";
 
 const  Home = ()=> {



  return (
    <div className="h-screen w-screen relative flex flex-col justify-center items-center">
       
   </div>
  );
}
export default isAuth(Home)
