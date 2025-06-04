"use client"

 
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { useMutation } from '@tanstack/react-query'
import { login } from '../controller/auth'
import { toast, ToastContainer } from 'react-toastify'
import iAuthResult from '../model/iAuthResult'
import Util from '../util/globle'
import { Label } from '@radix-ui/react-label'
import Link from 'next/link'

import isCanBackToLogin from '../middleware/isCanBackToLogin'
import { redirect, useRouter } from 'next/navigation'
import InputWithTitle from '@/components/ui/inputWithTitle'
export interface iLoginData {
    email: string;
    password: string;
}

const  Login = ()=> {
   const rout = useRouter() 
    const [data, setData] = useState<iLoginData>({
        email: 'ali@gmail.com',
        password: '12AS@#fs'
    });

    const loginFun = useMutation({
        mutationFn: (data: iLoginData) => login({ email: data.email, password: data.password }),
        onError: (e) => {
            // console.log(`error is ${e}`)
            toast.error(e.message)
        },
        onSuccess: (result) => {
            var resultData = result.data as iAuthResult
            Util.token = resultData.refreshToken
            rout.push("/dashboard")

        }
    })

    return (
        <div className="h-screen w-screen    flex  justify-center items-center">

            <div className='w-[400px] flex flex-col  items-center mb-20'>
                <label >SignIn</label>
                <div className='h-20' />

                <InputWithTitle
                    title='Email'
                    name={data.email}
                    placeHolder='Enter Your Email'
                    onchange={(value: string) => { setData((data) => ({ ...data, name: value })) }}
                />
                <div className='h-5' />
                <InputWithTitle
                    title='Password'
                    name={data.password}
                    placeHolder='Enter Your Password'
                    onchange={(value: string) => { setData((data) => ({ ...data, password: value })) }}
                />
                <div className='h-4' />
                <Button
                    disabled={
                        data.email.trim().length < 1 ||
                        data.password.trim().length < 1 ||
                        loginFun.isPending
                    }
                    className='bg-[#452CE8]'
                    onClick={() => loginFun.mutate(data)}
                >
                    SignIn
                </Button>
                <div className='h-4' />

                <div>
                    <Label>Have No Account </Label>
                    <Label>
                        <Link
                            href={'/signup'}
                        >Regester
                        </Link>
                    </Label>
                </div>
            </div>
            <ToastContainer />

        </div>
    )
}

export default  isCanBackToLogin(Login)