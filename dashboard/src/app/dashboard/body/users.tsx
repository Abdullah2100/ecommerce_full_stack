import InputWithTitle from "@/components/ui/input/inputWithTitle";
import { Label } from "@/components/ui/label";
import { useMutation, useQueries, useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { changeUserStatus, createVarient, deleteVarient, getUserAtPage, getUserPages, getVarient, updateVarient } from "../../../stores/data";
import Image from "next/image";
import { toast, ToastContainer } from "react-toastify";
import { Button } from "@/components/ui/button";

const Users = () => {
    const queryClient = useQueryClient()
    const { data: userPages } = useQuery({
        queryKey: ['usersPage'],
        queryFn: () => getUserPages()

    })

    const [currnetPage, setCurrentPage] = useState(1);

    const { data, refetch, isPlaceholderData } = useQuery({
        queryKey: ['users', currnetPage],
        queryFn: () => getUserAtPage(currnetPage)

    })

    useEffect(() => {
        queryClient.prefetchQuery({
            queryKey: ['users', currnetPage],
            queryFn: () => getUserAtPage(currnetPage),
        })
    }, [currnetPage])


    const changeUserStatusFun = useMutation(
        {
            mutationFn: (userId: string) => changeUserStatus(userId),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: (res) => {
                refetch()
                toast.success("تم التعديل بنجاح")


            }
        }
    )
    return (
        <div className="flex flex-col w-auto h-auto">
            <Label className="text-5xl">Users</Label>
            <div className="h-10" />

            {data != undefined && <div className="w-fit">

                <div className="p-3 max-w-[840px]">
                    {/* Table */}
                    <div className="overflow-x-auto  border-2 border-[#F0F2F5]  rounded-[9px]">
                        <table className="table-auto min-w-full ">
                            {/* Table header */}
                            <thead className="text-[13px]">
                                <tr
                                    className={`${data != undefined ? 'border-b-1 ' : undefined}`}>
                                    <th >
                                        <div className="font-medium text-left"></div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Theumbnail</div>
                                    </th>

                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Name</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Phone</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Email</div>
                                    </th>

                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left whitespace-nowrap">Has Store</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left whitespace-nowrap">Store Name</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left ">Actions</div>
                                    </th>

                                </tr>
                            </thead>
                            {/* Table body */}
                            <tbody className="text-sm font-medium">
                                {
                                    data?.map((value, index) => (
                                        <tr key={index}
                                            className={`${index != data.length - 1 ? 'border-b-1' : undefined}`}
                                        >
                                            <td className="ps-2 py-4">
                                                <div className="text-slate-500">{index + 1}</div>
                                            </td>
                                            <td className="py-4 px-10">
                                                <div  >
                                                    <img
                                                        className="h-6 w-6 rounded-full"
                                                        src={value.thumbnail}
                                                        alt="thumbnail"
                                                    />
                                                </div>
                                            </td>
                                            <td className="py-4 px-10">
                                                <div className="w-32">
                                                    <Label className="font-normal">
                                                        {value.name}
                                                    </Label>
                                                </div>
                                            </td>
                                            <td className="py-4 px-10">
                                                <div>
                                                    <Label className="font-normal">
                                                        {value.phone}
                                                    </Label>
                                                </div>
                                            </td>
                                            <td className="py-4 px-10">
                                                <div>
                                                    <Label className="font-normal">
                                                        {value.email}
                                                    </Label>
                                                </div>
                                            </td>
                                            <td className="py-4 px-10">
                                                <div>
                                                    <Label className="font-normal">
                                                        {value.store_id != null ? 'Yes' : 'NO'}
                                                    </Label>
                                                </div>
                                            </td>
                                            <td className="py-4 px-10 whitespace-nowrap">
                                                <div>
                                                    <Label className="font-normal">
                                                        {value.storeName}
                                                    </Label>
                                                </div>
                                            </td>
                                            <td className="py-4 px-10">
                                                <div>
                                                    {
                                                        value.isAdmin ?
                                                            <div className="w-full h-8 flex items-center justify-center bg-[#107980] rounded-4xl">
                                                                <Label className="text-white font-normal ">Me</Label>
                                                            </div> :
                                                            <Button
                                                                disabled={changeUserStatusFun.isPending}
                                                                onClick={() => changeUserStatusFun.mutate(value.id)}
                                                                className={`${!value.isActive ? 'bg-red-500' : 'bg-gray-400'} h-8 w-20`}>
                                                                <Label className="text-white text-[12px]">  {!value.isActive ? 'Bolck' : 'UnBlock'}</Label>

                                                            </Button>
                                                    }

                                                </div>
                                            </td>
                                        </tr>
                                    ))

                                }
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>}

            {userPages != undefined && <div className="flex flex-row w-full justify-center">

                {Array.from({ length: userPages }, (_, i) => (
                    <div
                        onClick={() => setCurrentPage(i + 1)}
                        className={`py-2 px-2 border-1 border-gray-500 rounded-full mr-2 ${currnetPage == i + 1 ? 'bg-[#452CE8]' : undefined} pointer-coarse:`}
                        key={i}><Label className={`${currnetPage == i + 1 ? 'text-white' : undefined}`}>{i + 1}</Label></div>
                ))}
            </div>}
            <ToastContainer />

        </div>
    );

};
export default Users;