using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ecommerc_dotnet.Migrations
{
    /// <inheritdoc />
    public partial class addingisBlockedtocategory : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "isBlocked",
                table: "Category",
                type: "boolean",
                nullable: false,
                defaultValue: false);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "isBlocked",
                table: "Category");
        }
    }
}
