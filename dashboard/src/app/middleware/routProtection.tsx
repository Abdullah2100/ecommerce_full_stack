import { useEffect } from "react";
import Util from "../util/globle";
import { redirect } from "next/navigation";

const isAuth = (Component: any) => {
  return function IsAuth(props: any) {
    const auth = Util.token.trim();

    useEffect(() => {
      if (auth.length != 0) {
        return redirect("/login");
      }
    }, []);

    if (auth.length === 0) {
      return null;
    }

    return <Component {...props} />;
  };
};

export default isAuth;
