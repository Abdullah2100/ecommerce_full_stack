import { NextRequest, NextResponse } from "next/server"
import Util from "../util/globle"
import { redirect } from "next/navigation"

const isAuth = (Component: any)=> {
  //const token = request.cookies.get('token')?.value
  const token = Util.token.trim() 

  if(token.length<1){
    return redirect("/login")
  }

  return <Component {...process}/>
}

export default isAuth;