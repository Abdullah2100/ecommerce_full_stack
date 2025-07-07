import InputWithTitle from "@/components/ui/inputWithTitle";
import { Label } from "@/components/ui/label";
import { keepPreviousData, useMutation, useQueries, useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import {
    getOrderAtPage,
    getOrderPages,
    getOrderStatusDefination,
    updateOrderStatus
} from "../controller/data";
import { toast, ToastContainer } from "react-toastify";
import { Button } from "@/components/ui/button";
import { DropdownMenu, DropdownMenuLabel, DropdownMenuTrigger } from "@radix-ui/react-dropdown-menu";
import {
    DropdownMenuContent,
    DropdownMenuItem,
} from "@/components/ui/dropdown-menu";
import iOrderStatusUpdateRequestDto from "../dto/request/iOrderStatusUpdateRequestDto";

const OrderPage = () => {

    const [currnetPage, setCurrentPage] = useState(1);
    const queryClient = useQueryClient()

    const { data: OrderStatusDefination } = useQuery({
        queryKey: ['OrderStatusDefination'],
        queryFn: () => getOrderStatusDefination(currnetPage),
        placeholderData: keepPreviousData
    })


    const { data: userPages } = useQuery({
        queryKey: ['orderPage'],
        queryFn: () => getOrderPages()

    })


    const { data, refetch, isPlaceholderData } = useQuery({
        queryKey: ['orders', currnetPage],
        queryFn: () => getOrderAtPage(currnetPage)

    })

    useEffect(() => {
        queryClient.prefetchQuery({
            queryKey: ['orders', currnetPage],
            queryFn: () => getOrderAtPage(currnetPage),
        })
    }, [currnetPage])

    const chageOrderStatus = useMutation(
        {
            mutationFn: (orderStatus: iOrderStatusUpdateRequestDto) => updateOrderStatus(orderStatus),
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
            <Label className="text-5xl">Orders</Label>
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
                                    <th className="py-4 px-10 whitespace-nowrap">
                                        <div>
                                            <Label className="font-normal">
                                                Owner Info
                                            </Label>
                                        </div>
                                    </th>
                                    <th className="py-4 px-10 whitespace-nowrap">
                                        <div>
                                            <Label className="font-normal">
                                                status
                                            </Label>
                                        </div>
                                    </th>
                                    <th className="py-4 px-10 whitespace-nowrap">
                                        <div>
                                            <Label className="font-normal">
                                                price
                                            </Label>
                                        </div>
                                    </th>
                                    <th className="py-4 px-10 whitespace-nowrap">
                                        <div>
                                            <Label className="font-normal">
                                                Actions
                                            </Label>
                                        </div>
                                    </th>
                                </tr>

                            </thead>
                            {/* Table body */}
                            <tbody className="text-sm font-medium">
                                {

                                    data?.map((value, index) => {
                                        // console.log(`this shown the list ${JSON.stringify(OrderStatusDefination)}`);

                                        return (
                                            <tr key={index}
                                                className={`${index != data.length - 1 ? 'border-b-1' : undefined}`}
                                            >
                                                <td className="ps-2 py-4">
                                                    <div className="text-slate-500">{index + 1}</div>
                                                </td>


                                                <td className="py-4 px-10">
                                                    <div className="flex flex-col">

                                                        <Label className="font-bold">
                                                            {value.name}
                                                        </Label>
                                                        <div className="h-2" />
                                                        <Label className="font-normal">
                                                            {value.user_phone}
                                                        </Label>
                                                    </div>
                                                </td>
                                                <td className="py-4 px-10">
                                                    <div className="flex flex-col">
                                                        <DropdownMenu>
                                                            <DropdownMenuTrigger asChild>
                                                                <Button variant="outline">{OrderStatusDefination ? OrderStatusDefination[value.status] : "undefined"}</Button>
                                                            </DropdownMenuTrigger>
                                                            <DropdownMenuContent className="w-56" align="start">
                                                                {
                                                                    OrderStatusDefination?.map((statusItem, index) => (

                                                                        <DropdownMenuItem
                                                                            onClick={
                                                                                () => chageOrderStatus.mutate({ id: value.id, statsu: index })
                                                                            }
                                                                            key={index}
                                                                            className="w-56">
                                                                            <DropdownMenuLabel>
                                                                                {statusItem}
                                                                            </DropdownMenuLabel>
                                                                        </DropdownMenuItem>

                                                                    ))
                                                                }
                                                            </DropdownMenuContent>
                                                        </DropdownMenu>
                                                    </div>
                                                </td>
                                                <td className="py-4 px-10 whitespace-nowrap">
                                                    <div className="flex flex-col">
                                                        <Label className="font-normal">
                                                            {'$ ' + value.totalPrice}
                                                        </Label>
                                                    </div>
                                                </td>
                                            </tr>
                                        )
                                    }

                                    )

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
export default OrderPage;
