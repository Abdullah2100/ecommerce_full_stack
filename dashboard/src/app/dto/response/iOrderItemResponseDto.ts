import iOrderProductDto from "./iOrderProductDto";
import iOrderProductVarientDto from "./iOrderProductVarientDto";

export default interface iOrderItemResponseDto{
    price:number,
    quanity:number,
    product:iOrderProductDto,
    productVarient:iOrderProductVarientDto
}