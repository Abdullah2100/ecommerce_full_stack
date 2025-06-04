import axios from "axios";
import Util from "../util/globle";
import { iUserInfo } from "../model/iUserInfo";
import { iUserUpdateInfoDto } from "../dto/iUserUpdateInfoDto";

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
