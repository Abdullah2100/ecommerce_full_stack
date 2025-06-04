import { iAddress } from "./IAddress";

export interface iUserInfo{
      Id:string
     name:string,
     phone:string
     email:string,
     thumbnail:string,
     address:iAddress[] 
     store_id:string 
}