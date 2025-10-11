import { Label } from "@/components/ui/label"
import { getMyInfo, updateUser } from "../../../stores/data"
import { useMutation, useQuery } from "@tanstack/react-query";
import InputWithTitle from "@/components/ui/input/inputWithTitle";
import Image from "next/image";
import { useRef, useState } from "react";
import { iUserUpdateInfoDto } from "../../../dto/response/iUserUpdateInfoDto";
import edite from '../../../../public/edite.svg'
import user from '../../../../public/user.svg'
import { Button } from "@/components/ui/button";
import { toast } from "react-toastify";
import { replaceUrlWithNewUrl } from "@/util/globle";

const MyInfoPage = () => {
    const { data, refetch } = useQuery({
        queryKey: ['myinfo'],
        queryFn: () => getMyInfo()
    })


    const [userUpdate, setUserUpdate] = useState<iUserUpdateInfoDto>({
        name:  "",
        phone:  "",
        newPassword: '',
        password: '',
        thumbnail: undefined,

    })
    const inputRef = useRef<HTMLInputElement>(null);


    const updateUserData = useMutation(
        {
            mutationFn: (userData: iUserUpdateInfoDto) => updateUser(userData),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: () => {
                refetch();
                toast.success("تم التعديل بنجاح");
                setUserUpdate(prev => ({
                    ...prev, 
                    thumbnail: undefined,
                    password: '',
                    newPassword: ''
                }));
            }
        }
    )
    return (
        <div className="flex flex-col w-[350px]">
            <Label className="text-5xl">My Info</Label>

            <div className="h-10" />

            <input type="file" hidden ref={inputRef}
                onChange={(e) => {
                    e.preventDefault()
                    if (e.target.files && e.target.files.length > 0) {
                        const file = e.target.files?.[0];
                        if (file != undefined) {
                            setUserUpdate((data) => ({ ...data, thumbnail: file }))
                        }
                    }
                }}
            />

            <div className="w-45 relative overflow-hidden">

                <div>
                    <div
                        className="h-40 w-40 rounded-[100px] border-2 flex justify-center items-center"
                    >
                        {data?.thumbnail != undefined && data?.thumbnail?.length > 0 ? <Image
                            className="h-30 w-30  rounded-full"
                            src={replaceUrlWithNewUrl(data.thumbnail)}
                            alt="userimage"
                            fill={true}
                        />
                            :
                            <img
                                className="h-30 w-30  rounded-full"
                                src={userUpdate.thumbnail != undefined ? URL.createObjectURL(userUpdate.thumbnail) : replaceUrlWithNewUrl(user.src)}
                                alt="userimage"
                            />
                        }
                    </div>
                    <Image
                        onClick={() => {
                            inputRef.current?.click();
                        }}
                        className="h-10 w-10 absolute bottom-0 right-5 bg-white cursor-pointer"
                        src={edite}
                        alt="userimage"
                        fill={false}
                    />
                </div>



            </div>
            <div className="h-5" />
            <InputWithTitle
                title='Name'
                name={userUpdate?.name ?? ""}
                placeHolder={data?.name??'Enter Your Name'}
                onchange={
                    (value: string) => { setUserUpdate((data) => ({ ...data, name: value })) }
                }
            />
            <div className="h-2" />


            <InputWithTitle
                title='Phone'
                name={userUpdate?.phone ?? ""}
                placeHolder={data?.phone??'Enter Your Phone'}
                onchange={
                    (value: string) => { setUserUpdate((data) => ({ ...data, phone: value ?? undefined })) }

                }
            />
            <div className="h-2" />

            <InputWithTitle
                title='Email'
                name={data?.email ?? ""}
                placeHolder='Enter Your Email'
            />
            <div className="h-2" />

            <InputWithTitle
                title='Current Password'
                name={userUpdate?.password ?? ""}
                placeHolder='Enter   Current Password'
                onchange={
                    (value: string) => { setUserUpdate((data) => ({ ...data, password: value ?? undefined })) }
                }
            />
            <div className="h-2" />

            <InputWithTitle
                title='New Password'
                name={userUpdate?.newPassword}
                placeHolder='Enter   New Password'
                onchange={
                    (value: string) => { setUserUpdate((data) => ({ ...data, newPassword: value ?? undefined })) }
                }
            />
            <div className="h-5" />

            <Button
                disabled={updateUserData.isPending}
                className='bg-[#452CE8]'
                onClick={() => updateUserData.mutate({
                    name: userUpdate.name !== data?.name ? userUpdate.name : data?.name ?? "",
                    phone: userUpdate.phone !== data?.phone ? userUpdate.phone : data?.phone ?? "",
                    password:  userUpdate.password ,
                    newPassword: userUpdate.newPassword,
                    thumbnail: userUpdate.thumbnail
                })}
            >
                update info
            </Button>

        </div>
    )
}
export default MyInfoPage