using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ecommerc_dotnet.Migrations
{
    /// <inheritdoc />
    public partial class makecategorynameuniqe : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateIndex(
                name: "IX_Category_name",
                table: "Category",
                column: "name",
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropIndex(
                name: "IX_Category_name",
                table: "Category");
        }
    }
}
