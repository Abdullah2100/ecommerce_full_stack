"use client"

import Image from 'next/image'
import Logo from '../../../public/logo.svg'
import { Input } from '@/components/ui/input'
import { useEffect, useState } from 'react'
import InputWithTitle from '@/components/inputWithTitle'
import { Button } from '@/components/ui/button'
import { useMutation } from '@tanstack/react-query'
import { login } from '../controller/auth'
import { toast, ToastContainer } from 'react-toastify'
import iAuthResult from '../model/iAuthResult'
import Util from '../util/globle'
import { redirect } from 'next/dist/server/api-utils'
interface iLoginData {
    name: string;
    password: string;
}

export default function Login() {
    const [data, setData] = useState<iLoginData>({
        name: 'ali@gmail.com',
        password: '12AS@#fs'
    });

    const loginFun = useMutation({
        mutationFn: (data: iLoginData) => login({ email: data.name, password: data.password }),
        onError: (e) => {
            // console.log(`error is ${e}`)
            toast.error(e.message)
        },
        onSuccess: (result) => {
            var resultData = result.data as iAuthResult
            Util.token = resultData.refreshToken
            
        }
    })

    return (
        <div className="h-screen w-screen    flex  justify-center items-center">

            <div className='w-[400px] flex flex-col  items-center mb-20'>
                <Image
                    className='h-24 w-40 object-contain'
                    src={Logo} alt={'logo'} />
                <div className='h-20' />

                <InputWithTitle
                    title='Email'
                    name={data.name}
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
                        data.name.trim().length < 1 ||
                        data.password.trim().length < 1 ||
                        loginFun.isPending
                    }
                    className='bg-[#452CE8]'
                    onClick={() => loginFun.mutate(data)}
                >
                    SignIn
                </Button>
            </div>
            <ToastContainer />

        </div>
    )
}