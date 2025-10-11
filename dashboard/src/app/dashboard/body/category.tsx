import { Label } from "@/components/ui/label";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useRef, useState } from "react";
import Image from "next/image";
import {
  createCategory,
  deleteCategory,
  getCategory,
  updateCategory,
} from "../../../stores/data";
import { toast } from "react-toastify";
import { Button } from "@/components/ui/button";
import { DeleteIcon } from "../../../../public/delete";
import iCategoryDto from "../../../dto/response/iCategoryDto";
import edite from "../../../../public/edite.svg";
import { replaceUrlWithNewUrl } from "@/util/globle";

const Category = () => {
  const [category, setCategory] = useState<iCategoryDto>({
    id: undefined,
    name: "",
    image: undefined,
  });
  const [fileUrlHolder, setFileUrlHolder] = useState("");
  const inputRef = useRef<HTMLInputElement>(null);
  const [currentPage] = useState(1);

  const { data, refetch } = useQuery({
    queryKey: ["categories"],
    queryFn: () => getCategory(currentPage),
  });

  const deleteCategoryFunc = useMutation({
    mutationFn: (id: string) => deleteCategory(id),
    onError: (e) => {
      toast.error(e.message);
    },
    onSuccess: () => {
      refetch();
      toast.success("تم الحذف بنجاح");
    },
  });
  const createCategoryFun = useMutation({
    mutationFn: (data: iCategoryDto) => createCategory(data),
    onError: (e) => {
      toast.error(e.message);
    },
    onSuccess: () => {
      refetch();
      toast.success("تمت الإضافة بنجاح");
      setCategory({ name: "", image: undefined });
      setFileUrlHolder("");
    },
  });

  const updateCategoryFun = useMutation({
    mutationFn: (data: iCategoryDto) => updateCategory(data),
    onError: (e) => {
      toast.error(e.message);
    },
    onSuccess: () => {
      refetch();
      toast.success("تم التعديل بنجاح");
      setCategory({ name: "", image: undefined });
      setFileUrlHolder("");
    },
  });

  return (
    <div className="flex flex-col w-auto h-auto">
      <Label className="text-5xl">Varient</Label>
      <input
        type="file"
        hidden
        ref={inputRef}
        onChange={(e) => {
          e.preventDefault();
          if (e.target.files && e.target.files.length > 0) {
            const file = e.target.files?.[0];
            if (file != undefined) {
              setCategory((data) => ({ ...data, image: file }));
            }
          }
        }}
      />

      <div className="h-10" />
      <div className="relative h-40 w-40">
        <div className="h-40 w-40 rounded-4xl border-2 flex justify-center items-center">
          {category.image === undefined && fileUrlHolder.length > 0 ? (
            <div className="relative w-full h-full">
              <Image
                src={fileUrlHolder}
                alt="Category preview"
                fill
                className="rounded-full object-cover"
              />
            </div>
          ) : category.image !== undefined ? (
            <div className="relative w-full h-full">
              <Image
                src={URL.createObjectURL(category.image)}
                alt="Category preview"
                fill
                className="rounded-full object-cover"
              />
            </div>
          ) : (
            <span className="text-gray-400">No image</span>
          )}
        </div>
        <button
          onClick={() => inputRef.current?.click()}
          className="h-10 w-10 absolute bottom-0 right-0 bg-white cursor-pointer rounded-full flex items-center justify-center border"
        >
          <div className="relative w-5 h-5">
            <Image src={edite} alt="Edit" fill className="object-contain" />
          </div>
        </button>
      </div>

      <div className="flex flex-col w-40 mt-4">
        <Label className="mb-2">Name</Label>
        <input
          type="text"
          maxLength={40}
          name={category.name}
          placeholder="Enter Your Category"
          onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
            setCategory((data) => ({ ...data, name: e.target.value }));
          }}
        />
        <div className="h-2" />

        <Button
          disabled={
            (category.name.trim().length < 1 && category.image != undefined) ||
            createCategoryFun.isPending ||
            deleteCategoryFunc.isPending
          }
          className="bg-[#452CE8]"
          onClick={() =>
            category.id == undefined
              ? createCategoryFun.mutate(category)
              : updateCategoryFun.mutate(category)
          }
        >
          {category.id != undefined ? "Update" : "Create"}
        </Button>
      </div>
      <div className="h-10" />

      {data != undefined && (
        <div className="w-fit">
          <div className="p-3">
            {/* Table */}
            <div className="overflow-x-auto border-2 border-[#F0F2F5]  rounded-[9px]">
              <table className="table-auto w-fit  ">
                {/* Table header */}
                <thead className="text-[13px]">
                  <tr
                    className={`${
                      data != undefined ? "border-b-1 " : undefined
                    }`}
                  >
                    <th>
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
                  {data !== undefined &&
                    data.length > 0 &&
                    data?.map((value, index) => {
                      return (
                        <tr
                          key={index}
                          className={`${
                            index != data.length - 1 ? "border-b-1" : undefined
                          }`}
                        >
                          <td className="ps-2 py-4">
                            <div className="text-slate-500">{index + 1}</div>
                          </td>
                          <td className="py-4 px-10">
                            <div>
                              {value.image !== undefined && (
                                <div className="relative h-12 w-12">
                                  {
                                    <Image
                                      src={replaceUrlWithNewUrl(value.image)}
                                      alt="thumbnail"
                                      fill
                                      className="rounded-full object-cover"
                                      loading="lazy"
                                    />
                                  }
                                </div>
                              )}
                            </div>
                          </td>

                          <td className="px-10">
                            <div className="text-slate-500">{value.name}</div>
                          </td>
                          <td>
                            <div className="flex flex-row">
                              <div
                                onClick={() => {
                                  setFileUrlHolder(value.image);
                                  setCategory({
                                    id: value.id,
                                    name: value.name,
                                  });
                                }}
                                className="bg-[#f5fafb] border border-[#107980] rounded-sm p-1 cursor-pointer"
                              >
                                <div className="relative h-6 w-6">
                                  <Image
                                    src={edite}
                                    alt="Edit"
                                    fill
                                    className="object-contain"
                                  />
                                </div>
                              </div>
                              <div className="w-2" />
                              <div
                                onClick={() =>
                                  deleteCategoryFunc.mutate(value.id ?? "")
                                }
                                className=" border-1 border-[#107980] rounded-sm relative"
                              >
                                <DeleteIcon className="h-6 w-6  fill-red-700   cursor-pointer" />
                              </div>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
export default Category;
