import axios from "axios";
import Util from "../util/globle";
import { iUserInfo } from "../model/iUserInfo";
import { iUserUpdateInfoDto } from "../dto/iUserUpdateInfoDto";
import { iVarient } from "../model/iVarient";
import iStore from "../model/iStore";
import iCategory from "../model/iCategory";
import iCategoryDto from "../dto/iCategoryDto";
import iProductResponseDto from "../dto/iProductResponseDto";

export async function getMyInfo() {
    const url = process.env.NEXT_PUBLIC_PASE_URL + '/api/User';
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as iUserInfo
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


export async function updateUser({
    name,
    newPassword,
    password,
    phone,
    thumbnail
}: iUserUpdateInfoDto) {
    const userData = new FormData()
    if (name.trim().length > 1)
        userData.append("name", name)
    if (phone.trim().length > 1)
        userData.append("phone", phone)
    if (thumbnail != undefined)
        userData.append("thumbnail", thumbnail)
    if (password != undefined)
        userData.append("password", password)
    if (newPassword != undefined)
        userData.append("newPassword", newPassword)

    const url = process.env.NEXT_PUBLIC_PASE_URL + '/api/User';
    console.log(`funtion is Called ${url}`)
    try {
        return await axios.put(url,
            userData, {
            headers: {
                'Authorization': `Bearer ${Util.token}`

            }
        }

        )
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


export async function getVarient(pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Varient/all/${pageNumber}`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as iVarient[]
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


export async function getVarientPageLenght() {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Varient/pages`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as number
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}

export async function deleteVarient(id: string) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Varient/${id}`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.delete(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            },
            validateStatus: (status) => status >= 200 && status < 300
        })

        if (result.status == 204) {
            return true
        } else {
            return false
        }

    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}

export async function createVarient(data: iVarient) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Varient`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.post(url, {
            id: data.id,
            name: data.name
        }, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            },
        })

        if (result.status == 204) {
            return true
        } else {
            return false
        }

    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


export async function updateVarient(data: iVarient) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Varient`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.put(url, {
            id: data.id,
            name: data.name
        }, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            },
        })

        if (result.status == 204) {
            return true
        } else {
            return false
        }

    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}



export async function getUserPages() {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/User/pages`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as number
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}



export async function getUserAtPage(pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/User/${pageNumber}`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return (result.data as iUserInfo[])
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


export async function changeUserStatus(userId: string) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/User/status/${userId}`;
    console.log(`funtion is Called ${url}`)
    console.log(`token ${userId}`)
    try {
        var result = await axios.delete(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as boolean
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}



//store

export async function getStorePages() {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Store/pages`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as number
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}



export async function getStoreAtPage(pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Store/${pageNumber}`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return (result.data as iStore[])
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}

export async function changeStoreStatus(store_id: string) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Store/status/${store_id}`;
    console.log(`funtion is Called ${url}`)
    console.log(`token ${store_id}`)
    try {
        var result = await axios.put(url, undefined, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            },

        })
        return result.data as boolean
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


//category


export async function getCategory(pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Category/all/${pageNumber}`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as iCategory[]
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


export async function createCategory(data: iCategoryDto) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Category`;
    console.log(`funtion is Called ${url}`)
    try {

        const dataHolder = new FormData();
        dataHolder.append("name", data.name)
        if (data.image != undefined)
            dataHolder.append("image", data.image)

        var result = await axios.post(url, dataHolder, {

            headers: {
                'Authorization': `Bearer ${Util.token}`
            },
        })

        if (result.status == 204) {
            return true
        } else {
            return false
        }

    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


export async function deleteCategory(id: string) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Category/${id}`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.delete(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            },
            validateStatus: (status) => status >= 200 && status < 300
        })

        if (result.status == 204) {
            return true
        } else {
            return false
        }

    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


export async function updateCategory(data: iCategoryDto) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Category`;
    console.log(`funtion is Called ${url}`)
    try {

        const dataHolder = new FormData();
        dataHolder.append("id", data.id!!);
        if (data.name.trim().length > 0)
            dataHolder.append("name", data.name)
        if (data.image != undefined)
            dataHolder.append("image", data.image)

        var result = await axios.put(url, dataHolder, {

            headers: {
                'Authorization': `Bearer ${Util.token}`
            },
        })

        if (result.status == 204) {
            return true
        } else {
            return false
        }

    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


//product

export async function getProductPages() {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Product/pages`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as number
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}


export async function getProductAtPage(pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Product/${pageNumber}`;
    console.log(`funtion is Called ${url}`)
    try {
        var result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return (result.data as iProductResponseDto[])
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}

