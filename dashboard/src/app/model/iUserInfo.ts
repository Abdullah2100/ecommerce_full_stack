import { iAddress } from "./IAddress";

export interface iUserInfo {
      id: string
      name: string,
      phone: string
      email: string,
      thumbnail: string,
      address: iAddress[]
      store_id?: string,
      isActive: boolean,
      isAdmin: boolean
}