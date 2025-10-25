using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.extentions;

public static class OrderExperientialExtension
{
    public static OrderVarientDto ToOrderVarientDto(this OrderProductsVarient orderProductsVariant)
    {
        return new OrderVarientDto
        {
            Name = orderProductsVariant.ProductVariant.Product.Name,
            VarientName = orderProductsVariant.ProductVariant.Varient.Name,

        };
    }
    
}