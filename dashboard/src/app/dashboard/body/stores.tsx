import InputWithTitle from "@/components/ui/input/inputWithTitle";
import { Label } from "@/components/ui/label";
import { useMutation, useQueries, useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { toast, ToastContainer } from "react-toastify";
import { Button } from "@/components/ui/button";
import { changeStoreStatus, getStoreAtPage, getStorePages } from "@/lib/api/store";

const Stores = () => {
    const queryClient = useQueryClient()
    const { data: storePages } = useQuery({
        queryKey: ['storePages'],
        queryFn: () => getStorePages()

    })

    const [currnetPage, setCurrentPage] = useState(1);

    const { data, refetch, isPlaceholderData } = useQuery({
        queryKey: ['stores', currnetPage],
        queryFn: () => getStoreAtPage(currnetPage)

    })

    useEffect(() => {
        queryClient.prefetchQuery({
            queryKey: ['stores', currnetPage],
            queryFn: () => getStoreAtPage(currnetPage),
        })
    }, [currnetPage])


    const changeStoreStatusFun = useMutation(
        {
            mutationFn: (store_id: string) => changeStoreStatus(store_id),
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
            <Label className="text-5xl">Stores</Label>
            <div className="h-10" />

            {data != undefined && <div className="w-fit">

                <div className="p-3 max-w-[740px] overflow-hidden">
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
                                        <div className="font-medium text-left">Owner Name</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left whitespace-nowrap">Created At</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">isBlock</div>
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
                                                        src={value.small_image}
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
                                                        {value.userName}
                                                    </Label>
                                                </div>
                                            </td>


                                            <td className="py-4 px-10">
                                                <div>
                                                    <Label className="font-normal">
                                                        {(value.created_at.toString().split('T')[0]).replaceAll('-','/')}
                                                    </Label>
                                                </div>
                                            </td>

                                            <td className="py-4 px-10">
                                                <div>
                                                    {

                                                        <Button
                                                            disabled={changeStoreStatusFun.isPending}
                                                            onClick={() => changeStoreStatusFun.mutate(value.id)}
                                                            className={`${value.isBlocked ? 'bg-red-500' : 'bg-gray-400'} h-8 w-20`}>
                                                            <Label className="text-white text-[12px]">  {value.isBlocked ? 'Bolck' : 'UnBlock'}</Label>

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

            {storePages != undefined && <div className="flex flex-row w-full justify-center items-center">

                {Array.from({ length: storePages }, (_, i) => (
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
export default Stores;