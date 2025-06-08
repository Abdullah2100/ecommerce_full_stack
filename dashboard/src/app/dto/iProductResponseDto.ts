import iProductVarientDto from "./iProductVarientDto";

export default interface iProductResponseDto{
    id:string,
    name:string,
    description:string,
    thmbnail:string,
    subcategory:string,
    store:string,
    price:number,
    productVarients:iProductVarientDto[][]
}