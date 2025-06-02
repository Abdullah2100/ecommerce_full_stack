import axios from "axios";


interface iLoginProp {
    email: string;
    password: string;
}

export async function login({ email, password }: iLoginProp) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + '/api/User/login';
    console.log(`funtion is Called ${url}`)
    try {
        return await axios.post(url,
            {
                username: email,
                password: password
            },

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