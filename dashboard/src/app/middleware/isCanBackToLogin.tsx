import { useEffect } from "react";
import Util from "../util/globle"
import { redirect } from "next/navigation"

const isCanBackToLogin = (Component: any) => {
 return function isCanBackToLogin(props: any) {
    const auth = Util.token.trim();


    useEffect(() => {
      if (auth.length > 0) {
        return redirect("/");
      }
    }, []);


    if (auth.length > 0) {
      return null;
    }

    return <Component {...props} />;
  }; 
}

export default isCanBackToLogin;