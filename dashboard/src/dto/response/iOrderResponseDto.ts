import iOrderItemResponseDto from "./iOrderItemResponseDto";

    interface IOrderResponseDto {
    id: string,
    name: string,
    user_phone: string,
    status: number,
    totalPrice: number,
    order_items: iOrderItemResponseDto[]
}

interface IAdminReposeDto{
    orders: IOrderResponseDto[],
    pageNum:number
}
export type {
    IOrderResponseDto,
    IAdminReposeDto
}