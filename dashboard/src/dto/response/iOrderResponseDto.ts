import iOrderItemResponseDto from "./iOrderItemResponseDto";

export default interface iOrderResponseDto {
    id: string,
    name: string,
    user_phone: string,
    status: number,
    totalPrice: number,
    order_items: iOrderItemResponseDto[]
}