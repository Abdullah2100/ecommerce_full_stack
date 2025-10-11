import InputWithTitle from "@/components/ui/input/inputWithTitle";
import { Label } from "@/components/ui/label";
import { useMutation, useQueries, useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { changeUserStatus, createVarient, deleteVarient, getProductAtPage, getProductPages, getUserAtPage, getUserPages, getVarient, updateVarient } from "../../../stores/data";
import Image from "next/image";
import { toast, ToastContainer } from "react-toastify";
import { Button } from "@/components/ui/button";

const Product = () => {
    const queryClient = useQueryClient()
    const { data: userPages } = useQuery({
        queryKey: ['usersPage'],
        queryFn: () => getProductPages()

    })

    const [currnetPage, setCurrentPage] = useState(1);

    const { data, refetch, isPlaceholderData } = useQuery({
        queryKey: ['products', currnetPage],
        queryFn: () => getProductAtPage(currnetPage)

    })

    useEffect(() => {
        queryClient.prefetchQuery({
            queryKey: ['products', currnetPage],
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
            <Label className="text-5xl">Products</Label>
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
                                        <div className="font-medium text-left">Price</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Store Name</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Sub Category</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Product Varient</div>
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
                                                        src={value.thmbnail}
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
                                                <div className="w-32">
                                                    <Label className="font-normal">
                                                        {value.price}
                                                    </Label>
                                                </div>
                                            </td>
                                            <td className="py-4 px-10">
                                                <div className="w-32">
                                                    <Label className="font-normal">
                                                        {value.store}
                                                    </Label>
                                                </div>
                                            </td>

                                            <td className="py-4 px-10">
                                                <div className="w-32">
                                                    <Label className="font-normal">
                                                        {value.subcategory}
                                                    </Label>
                                                </div>
                                            </td>
                                            <td className="py-4 px-10 whitespace-nowrap">
                                                <div className="w-32">

                                                    {value.productVarients.map((value, index) => (
                                                        value.map((insidV, iIndex) => (
                                                            <div className="flex flex-row">
                                                                {iIndex == 0 &&
                                                                    <Label>
                                                                        {insidV.varientName+': '}
                                                                    </Label>}
                                                                <div className="ms-4 flex flex-row">
                                                                    <Label>{insidV.name}</Label>
                                                                    <Label>{insidV.precentage}</Label>
                                                                </div>
                                                            </div>
                                                        ))
                                                    ))}
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
export default Product;