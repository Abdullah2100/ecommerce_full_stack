import InputWithTitle from "@/components/ui/inputWithTitle";
import { Label } from "@/components/ui/label";
import { useMutation, useQueries, useQuery } from "@tanstack/react-query";
import { useRef, useState } from "react";
import { createCategory, createVarient, deleteCategory, deleteVarient, getCategory, getVarient, updateCategory, updateVarient } from "../controller/data";
import Image from "next/image";
import { toast, ToastContainer } from "react-toastify";
import { iVarient } from "../model/iVarient";
import { Button } from "@/components/ui/button";
import { EditeIcon } from "../../../public/editeIcon";
import { DeleteIcon } from "../../../public/delete";
import iCategoryDto from "../dto/iCategoryDto";
import edite from '../../../public/edite.svg'

const CategoryPage = () => {
    const [category, setCategory] = useState<iCategoryDto>({
        id: undefined,
        name: '',
        image: undefined
    });
    const [fileUrlHolder, setFileUrlHolder] = useState('')


    const inputRef = useRef<HTMLInputElement>(null);

    const [currnetPage, setCurrentPage] = useState(1);


    const { data, refetch } = useQuery({
        queryKey: ['categoies'],
        queryFn: () => getCategory(currnetPage)

    })

    const deleteCategoryFunc = useMutation(
        {
            mutationFn: (id: string) => deleteCategory(id),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: (res) => {
                refetch()
                toast.success("تم  الحذف بنجاح")
            }
        }
    )
    const createCategoryFun = useMutation(
        {
            mutationFn: (data: iCategoryDto) => createCategory(data),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: (res) => {
                refetch()
                toast.success("تم  الاضافة بنجاح")
                setCategory({ name: '', image: undefined })
            }
        }
    )

    const updateCategoryFun = useMutation(
        {
            mutationFn: (data: iVarient) => updateCategory(data),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: (res) => {
                refetch()
                toast.success("تم  التعديل بنجاح")
                setCategory({ name: '', image: undefined })

            }
        }
    )


    return (
        <div className="flex flex-col w-auto h-auto">
            <Label className="text-5xl">Varient</Label>
            <input type="file" hidden ref={inputRef}
                onChange={(e) => {
                    e.preventDefault()
                    if (e.target.files && e.target.files.length > 0) {
                        const file = e.target.files?.[0];
                        if (file != undefined) {
                            setCategory((data) => ({ ...data, image: file }))
                        }
                    }
                }}
            />

            <div className="h-10" />
            <div className="relative h-40 w-40">
                <div
                    className="h-40 w-40 rounded-4xl border-2 flex justify-center items-center"
                >
                    {
                       category.image==undefined&& fileUrlHolder.length > 0 ? <Image
                            className="h-30 w-30  rounded-full"
                            src={fileUrlHolder}
                            alt="userimage"
                            fill={true}
                        />
                            :
                            category.image != undefined && <img
                                className="h-30 w-30  rounded-full"
                                src={URL.createObjectURL(category.image)}
                                alt="userimage"
                            />
                    }
                </div>
                <Image
                    onClick={() => {
                        inputRef.current?.click();
                    }}
                    className="h-10 w-10 absolute bottom-0 right-0 bg-white cursor-pointer"
                    src={edite}
                    alt="userimage"
                    fill={false}
                />
            </div>



            <div className="flex flex-col w-40">
                <InputWithTitle
                    maxLength={40}
                    title="Name"
                    name={category.name}
                    placeHolder="Enter Your Category"
                    onchange={
                        (value: string) => { setCategory((data) => ({ ...data, name: value })) }
                    }
                />
                <div className="h-2" />

                <Button
                    disabled={(category.name.trim().length < 1 && category.image != undefined) || createCategoryFun.isPending || deleteCategoryFunc.isPending}
                    className='bg-[#452CE8]'
                    onClick={() => category.id == undefined ? createCategoryFun.mutate(category) : updateCategoryFun.mutate(category)

                    }
                >
                    {category.id != undefined ? 'Update' : 'Create'}
                </Button>
            </div>
            <div className="h-10" />

            {data != undefined && <div className="w-fit">

                <div className="p-3">
                    {/* Table */}
                    <div className="overflow-x-auto border-2 border-[#F0F2F5]  rounded-[9px]">
                        <table className="table-auto w-fit  ">
                            {/* Table header */}
                            <thead className="text-[13px]">
                                <tr
                                    className={`${data != undefined ? 'border-b-1 ' : undefined}`}>
                                    <th >
                                        <div className="font-medium text-left"></div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Image</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Name</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Action</div>
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
                                                <div>

                                                    <img
                                                        className="h-12 w-12 rounded-full"
                                                        src={value.image_path}
                                                        alt="thumbnail"
                                                    />
                                                </div>
                                            </td>

                                            <td className="px-10">
                                                <div className="text-slate-500">{value.name}</div>
                                            </td>
                                            <td>
                                                <div className="flex flex-row">
                                                    <div
                                                        onClick={() => {
                                                            setFileUrlHolder(value.image_path)
                                                            setCategory({ id: value.id, name: value.name })
                                                        }
                                                        }
                                                        className="bg-[#f5fafb] border-1 border-[#107980] rounded-sm">
                                                        <EditeIcon
                                                            className="h-6 w-6 fill-[#107980] cursor-pointer mt-0 "
                                                        />
                                                    </div>
                                                    <div className="w-2" />
                                                    <div
                                                        onClick={() => deleteCategoryFunc.mutate(value.id ?? "")}
                                                        className=" border-1 border-[#107980] rounded-sm relative">
                                                        <DeleteIcon
                                                            className="h-6 w-6  fill-red-700   cursor-pointer"
                                                        />
                                                    </div>
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
            <ToastContainer />

        </div>
    );

};
export default CategoryPage;